package hibernate_examples.lang.pool;

import com.google.common.collect.ImmutableSet;

public class MultiNotifier implements Notifier {
    
    private final ImmutableSet<Notifier> notifiers;

    public MultiNotifier(Notifier... notifiers) {
        this(ImmutableSet.copyOf(notifiers));
    }

    public MultiNotifier(ImmutableSet<Notifier> notifiers) {
        this.notifiers = notifiers;
    }

    @Override
    public void entryEvent(EventType event, String poolId, String id) {
        notifiers.stream().forEach( n -> n.entryEvent(event, poolId, id));
    }

    @Override
    public void poolEvent(EventType event, String poolId) {
        notifiers.stream().forEach( n -> n.poolEvent(event, poolId));
    }
}
