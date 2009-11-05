package org.apache.commons.collections.map;

/**
* map implementation based on LinkedMap that maintains a sorted list of
* values for iteration
*/
public class ValueSortedHashMap extends LinkedMap {
private final boolean _asc;

// don't use super()!
public ValueSortedHashMap(final boolean asc) {
super(DEFAULT_CAPACITY);
_asc = asc;
}

// SNIP: some more constructors with initial capacity and the like

protected void addEntry(final HashEntry entry, final int hashIndex) {
final LinkEntry link = (LinkEntry) entry;
insertSorted(link);
data[hashIndex] = entry;
}

protected void updateEntry(final HashEntry entry,
final Object newValue) {
entry.setValue(newValue);
final LinkEntry link = (LinkEntry) entry;
link.before.after = link.after;
link.after.before = link.before;
link.after = link.before = null;
insertSorted(link);
}

private void insertSorted(final LinkEntry link) {
LinkEntry cur = header;
// iterate whole list, could (should?) be replaced with quicksearch
// start at end to optimize speed for in-order insertions
while ((cur = cur.before) != header && !insertAfter(cur, link)) {
}
link.after = cur.after;
link.before = cur;
cur.after.before = link;
cur.after = link;
}

protected boolean insertAfter(final LinkEntry cur,
final LinkEntry link) {
if (_asc) {
return ((Comparable) cur.getValue()).compareTo( link.getValue()) <= 0;
} else {
return ((Comparable) cur.getValue()).compareTo( link.getValue()) >= 0;
}
}

public boolean isAscending() {
return _asc;
}
}