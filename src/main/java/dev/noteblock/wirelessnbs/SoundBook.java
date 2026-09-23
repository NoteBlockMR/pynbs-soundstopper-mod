package dev.noteblock.wirelessnbs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** Instance identity is independent of the sound event/OGG name. Client-thread only. */
public final class SoundBook<T> {
    public record Key(String song, long note) {}
    public record Entry<T>(Key key, int layer, T instance, long startedAt) {}
    private final Map<Key, Entry<T>> active = new LinkedHashMap<>();
    private final Consumer<T> stop;
    private final int capacity;

    public SoundBook(Consumer<T> stop, int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("capacity must be positive");
        this.stop = stop;
        this.capacity = capacity;
    }

    public void add(String song, long note, int layer, T instance, long tick) {
        if (layer < 1 || note < 0) throw new IllegalArgumentException("Invalid note/layer");
        stopNote(song, note);
        if (active.size() >= capacity) {
            var oldest = active.values().iterator().next();
            stopNote(oldest.key().song(), oldest.key().note());
        }
        var key = new Key(song, note);
        active.put(key, new Entry<>(key, layer, instance, tick));
    }

    public void stopNote(String song, long note) {
        var removed = active.remove(new Key(song, note));
        if (removed != null) stop.accept(removed.instance());
    }

    /** NBS range is 1-based, inclusive. Start 0 stops the entire named song. */
    public void stopRange(String song, int start, int end) {
        int first = Math.max(0, start);
        int last = Math.max(first, end);
        removeIf(entry -> entry.key().song().equals(song)
            && (first == 0 || entry.layer() >= first && entry.layer() <= last), true);
    }

    public void prune(long tick, Predicate<T> isPlaying) {
        // SoundManager queues playback; don't mistake a not-yet-started sound for a finished one.
        removeIf(entry -> tick - entry.startedAt() >= 5 && !isPlaying.test(entry.instance()), false);
    }

    public void clear() { removeIf(entry -> true, true); }
    public int size() { return active.size(); }

    private void removeIf(Predicate<Entry<T>> predicate, boolean stopAudio) {
        var iterator = active.values().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (predicate.test(entry)) {
                if (stopAudio) stop.accept(entry.instance());
                iterator.remove();
            }
        }
    }
}
