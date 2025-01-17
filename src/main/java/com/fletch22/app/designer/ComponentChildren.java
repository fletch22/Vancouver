package com.fletch22.app.designer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class ComponentChildren {

	private static Logger logger = LoggerFactory.getLogger(ComponentChildren.class);

	private ArrayList<Child> children = new ArrayList<Child>();
	private boolean haveChildrenBeenResolved = false;

	public ImmutableList<Child> getList() {
		return ImmutableList.copyOf(children);
	}

	public boolean isHaveChildrenBeenResolved() {
		return haveChildrenBeenResolved;
	}

	public void setHaveChildrenBeenResolved(boolean haveChildrenBeenResolved) {
		this.haveChildrenBeenResolved = haveChildrenBeenResolved;
	}

	public void addChildIgnoreOrdinal(Child child) {
		this.children.add(child);
	}

	public void addChildAtOrdinal(Child child, long ordinal) {
		logger.debug("Adding child at index: {}", ordinal);
		if (ordinal == Child.ORDINAL_LAST) {
			ordinal = this.children.size();
		}

		sortByOrdinal();

		this.children.add((int) ordinal, child);
		for (int i = 0; i < this.children.size(); i++) {
			Child childReset = this.children.get(i);
			childReset.setOrdinal(String.valueOf(i));

			logger.debug("New child reset: {}", childReset.getOrdinal());
		}
	}

	public void clear() {
		this.children.clear();
	}

	public void removeChild(Child child) {
		validateChildrenResolved();

		if (!this.children.contains(child)) {
			throw new RuntimeException("Encountered problem while trying to remove child. Child is not in parent.");
		}
		

		this.children.remove(child);
		this.resetChildrensOrdinals();
	}

	public void resetChildrensOrdinals() {
		this.sortByOrdinal();
		logger.info("Size children: {}", this.children.size());
		for (int i = 0; i < this.children.size(); i++) {
			Child child = this.children.get(i);
			child.setOrdinal(String.valueOf(i));
		}
	}

	public Child findChildById(long childId) {
		validateChildrenResolved();

		Optional<Child> childFound = this.children.stream().filter(child -> (child.getId() == childId)).collect(Collectors.reducing((a, b) -> {
			throw new RuntimeException("Encountered problem while trying to remove child. More than one child with that ID found in parent.");
		}));

		if (!childFound.isPresent()) {
			throw new RuntimeException(String.format("Encountered problem while trying to find child. Child with id %s could not be found in parent", childId));
		}

		return childFound.get();
	}

	public void validateChildrenResolved() {
		if (!haveChildrenBeenResolved) {
			throw new RuntimeException(
					"Encountered problem while trying to remove child. Child is not in parent because children have not been 'resolved'. This is a programming error.");
		}
	}

	public void sort() {
		Collections.sort(this.children, new Comparator<Child>() {
			@Override
			public int compare(Child p1, Child p2) {
				int result = 0;
				if (p1.getId() > p2.getId()) {
					result = 1;
				} else if (p1.getId() < p2.getId()) {
					result = -1;
				}
				return result;
			}
		});
	}

	public void sortByOrdinal() {
		this.children = this.children.stream().sorted((child1, child2) -> {
			int ordinal1 = (int) child1.getOrdinalAsNumber();
			int ordinal2 = (int) child2.getOrdinalAsNumber();
			int result = 0;
			if (ordinal1 > ordinal2) {
				result = 1;
			} else if (ordinal1 < ordinal2) {
				result = -1;
			}
			return result;
		}).collect(Collectors.toCollection(ArrayList::new));
	}
}
