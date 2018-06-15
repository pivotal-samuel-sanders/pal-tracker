package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private final HashMap<Long, TimeEntry> dataStore = new HashMap<>();
    private final IdGenerator idGenerator = new IdGenerator();

    public TimeEntry create(TimeEntry timeEntry) {
        long id = idGenerator.getNextId();
        timeEntry.setId(id);

        dataStore.put(timeEntry.getId(), timeEntry);

        return dataStore.get(timeEntry.getId());
    }

    public TimeEntry find(long id) {
        return dataStore.get(id);
    }

    public List<TimeEntry> list() {
        return new ArrayList<>(dataStore.values());
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        timeEntry.setId(id);
        dataStore.put(id, timeEntry);
        return dataStore.get(id);
    }

    public TimeEntry delete(long id) {
        return dataStore.remove(id);
    }

    private class IdGenerator {
        private long nextId = 0;

        private IdGenerator() {
        }

        private long getNextId() {
            return ++nextId;
        }
    }
}
