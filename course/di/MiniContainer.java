package course.di;

import java.lang.annotation.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

/** Small DI container: constructor injection, named beans, scopes, cycle detection. */
public final class MiniContainer implements AutoCloseable {
    public enum Scope { SINGLETON, PROTOTYPE }
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface MiniNamed { String value(); }
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.CONSTRUCTOR)
    public @interface MiniInject {}
    private record Definition(String name, Class<?> type, Scope scope, Object external) {}
    private final Map<String, Definition> definitions = new LinkedHashMap<>();
    private final Map<String, Object> ready = new HashMap<>();
    private final List<String> path = new ArrayList<>();
    private boolean started;

    public void register(String name, Class<?> type, Scope scope) {
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers()) ||
                type.isMemberClass() && !Modifier.isStatic(type.getModifiers())) {
            throw new IllegalArgumentException("Unsupported class " + type.getName());
        }
        put(new Definition(name, type, scope, null));

    }
    public void registerInstance(String name, Object instance) {
        Objects.requireNonNull(instance);
        put(new Definition(name, instance.getClass(), Scope.SINGLETON, instance));
    }
    private void put(Definition definition) {
        if (started) throw new IllegalStateException("Already started");
        if (definition.name() == null || definition.name().isBlank()) throw new IllegalArgumentException("Name required");
        if (definitions.putIfAbsent(definition.name(), definition) != null) throw new IllegalArgumentException("Duplicate name");
    }
    public void start() { started = true; }
    public <T> T getBean(Class<T> type) { return getBean(type, null); }
    public <T> T getBean(Class<T> type, String name) {
        if (!started) throw new IllegalStateException("Call start first");
        List<Definition> candidates = definitions.values().stream()
            .filter(d -> type.isAssignableFrom(d.type()))
            .filter(d -> name == null || name.equals(d.name())).toList();
        if (candidates.size() != 1) throw new IllegalStateException("Expected one candidate, got " + candidates.size());
        Definition definition = candidates.get(0);
        if (definition.external() != null) return type.cast(definition.external());
        if (ready.containsKey(definition.name())) return type.cast(ready.get(definition.name()));
        if (path.contains(definition.name())) {
            throw new IllegalStateException("Cycle: " + String.join(" -> ", path) + " -> " + definition.name());
        }
        path.add(definition.name());
        try {
            Constructor<?>[] constructors = definition.type().getConstructors();
            Constructor<?> constructor = null;
            if (constructors.length == 1) {
                 constructor = constructors[0];
            } else {
                int marked = 0;
                for (Constructor<?> candidate : constructors) {
                    if (candidate.isAnnotationPresent(MiniInject.class)) {
                        marked ++;
                        constructor = candidate;
                    }
                }
               if (marked != 1) {
                   throw new IllegalArgumentException("Exactly one @MiniInject constructor required");
               }
            }
            Parameter[] parameters = constructor.getParameters();
            Object[] arguments = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                MiniNamed mark = parameters[i].getAnnotation(MiniNamed.class);
                String dependencyName = null;
                if (mark != null) {
                    dependencyName = mark.value();
                } else {
                    dependencyName = null;
                }
                arguments[i] = getBean(parameters[i].getType(), dependencyName);
            }
            Object object = constructor.newInstance(arguments);
            if (definition.scope() == Scope.SINGLETON) {
                ready.put(definition.name(), object);
            }

            return type.cast(object);
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("Cannot create " + definition.type().getName(), failure);
        } finally {
            path.remove(path.size() - 1);
        }

    }
    // No resource ownership or lifecycle here. M23 introduces their separate contract.
    public void close() { ready.clear(); definitions.clear(); started = false; }
}
