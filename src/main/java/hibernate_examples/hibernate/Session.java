package hibernate_examples.hibernate;

import hibernate_examples.lang.pool.Reusable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.ofNullable;

public class Session implements Reusable {

    private final org.hibernate.Session session;
    private final org.hibernate.Transaction transaction;

    Session(org.hibernate.Session session) {
        this.session = session;
        this.transaction = session.beginTransaction();
    }

    public <T extends Entity> Optional<T> get(Class<T> type, UUID id) {
        return ofNullable(doGet(type, id));
    }

    public <T extends Entity> T load(Class<T> type, UUID id) {
        return checkNotNull(
                doGet(type, id),
                "Failed to load %s for id %s", type, id
        );
    }

    @SuppressWarnings("unchecked")
    private <T extends Entity> T doGet(Class<T> type, UUID id) {
        return (T) session.get(type, id);
    }

    public <T extends Entity> List<T> loadAll(Class<T> type) {
        return unmodifiableList(session.createCriteria(type).list());
    }

    public <T extends Entity> T save(T transientEntity) {
        session.saveOrUpdate(transientEntity);
        return transientEntity;
    }

    public <T extends Entity> T attach(T transientEntity) {
        session.update(transientEntity);
        return transientEntity;
    }

    public void delete(Entity entity) {
        session.delete(entity);
    }

    void close() {
        try {
            endTransaction();
        } finally {
            session.close();
        }
    }

    public void flush() {
        session.flush();
    }

    @Override
    public State check() {
        return State.OK;
    }

    @Override
    public void onError() {
        transaction.rollback();
        session.clear();
    }

    @Override
    public void reset() {
        endTransaction();
    }

    private void endTransaction() {
        flush();
        if (transaction.isActive()) {
            transaction.commit();
        }
    }
}
