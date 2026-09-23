package dev.noteblock.wirelessnbs;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SoundBookTest {
    @Test void sameSoundEventOnTwoLayersHasIndependentInstances() {
        var stopped = new ArrayList<Object>();
        var book = new SoundBook<Object>(stopped::add, 100);
        var first = new Object(); var second = new Object();
        book.add("song", 1, 1, first, 0);
        book.add("song", 2, 2, second, 0);
        book.stopRange("song", 1, 1);
        assertEquals(java.util.List.of(first), stopped);
        assertEquals(1, book.size());
    }
    @Test void stopNoteDoesNotStopOtherNotesOnSameLayerOrOtherSong() {
        var stopped = new ArrayList<String>();
        var book = new SoundBook<String>(stopped::add, 100);
        book.add("a", 1, 1, "a1", 0); book.add("a", 2, 1, "a2", 0);
        book.add("b", 1, 1, "b1", 0);
        book.stopNote("a", 1);
        assertEquals(java.util.List.of("a1"), stopped);
        book.stopRange("a", 0, 0);
        assertEquals(java.util.List.of("a1", "a2"), stopped);
        assertEquals(1, book.size());
    }
    @Test void reversedRangeClampsToStartAndSameTickOrderIsPreserved() {
        var stopped = new ArrayList<String>();
        var book = new SoundBook<String>(stopped::add, 100);
        book.add("a", 1, 42, "before", 0);
        book.stopRange("a", 42, 1);
        book.add("a", 2, 42, "after", 0);
        assertEquals(java.util.List.of("before"), stopped);
        assertEquals(1, book.size());
    }
    @Test void replayedNoteReplacesOnlyItsOwnInstance() {
        var stopped = new ArrayList<String>();
        var book = new SoundBook<String>(stopped::add, 100);
        book.add("a", 1, 1, "old", 0); book.add("a", 1, 1, "new", 1);
        assertEquals(java.util.List.of("old"), stopped);
        book.clear(); assertEquals(java.util.List.of("old", "new"), stopped);
    }
    @Test void pruneAllowsQueuedAudioTimeToStartAndKeepsLongSounds() {
        var book = new SoundBook<String>(s -> fail("Must not stop completed audio"), 100);
        book.add("a", 1, 1, "queued", 0);
        book.prune(4, s -> false); assertEquals(1, book.size());
        book.prune(1000, s -> true); assertEquals(1, book.size());
        book.prune(1001, s -> false); assertEquals(0, book.size());
    }
    @Test void boundedRegistryStopsOldestInstance() {
        var stopped = new ArrayList<String>();
        var book = new SoundBook<String>(stopped::add, 1);
        book.add("a", 1, 1, "old", 0); book.add("a", 2, 2, "new", 0);
        assertEquals(java.util.List.of("old"), stopped);
        assertEquals(1, book.size());
    }
}
