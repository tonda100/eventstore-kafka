import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.Test;

import net.osomahe.esk.eventstore.entity.EventStoreEvent;
import net.osomahe.esk.eventstore.entity.EventGroupKey;


/**
 * TODO write JavaDoc
 *
 * @author Antonin Stoklasek
 */
public class GroupKeyTest {


    @Test
    public void testExpiration(){
        long epoch = ZonedDateTime.now().toEpochSecond();
        System.out.println(epoch);

        System.out.println(ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond());
    }

    @Test
    public void testGroupKey() {
        EventB event = new EventB();
        event.setUserId("user123");
        event.setAge(18L);
        event.setName("my event");

        System.out.println(getGroupKeyValue(event));
    }

    private <T extends EventStoreEvent> Object getGroupKeyValue(T event) {
        return getGroupKeyValue(event, event.getClass());
    }

    private <T extends EventStoreEvent> Object getGroupKeyValue(T event, Class<?> eventClass) {
        for (Field f : eventClass.getDeclaredFields()) {
            if (f.isAnnotationPresent(EventGroupKey.class)) {
                try {
                    if (!f.isAccessible()) {
                        f.setAccessible(true);
                    }
                    return f.get(event);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Method method : eventClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventGroupKey.class)) {
                try {
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    return method.invoke(event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        if (eventClass.getSuperclass() != null) {
            return getGroupKeyValue(event, eventClass.getSuperclass());
        }
        return null;
    }
}
