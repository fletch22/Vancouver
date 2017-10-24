package com.fletch22.app.designer;

import static org.junit.Assert.*

import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.app.designer.submit.ButtonSubmit
import com.google.common.collect.ImmutableList

@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
class ComponentChildrenSpec extends Specification {

	def 'Set ordinals when all children have ordinal values'() {
		given:
		ComponentChildren componentChildren = new ComponentChildren()
		
		ButtonSubmit buttonSubmit0 = new ButtonSubmit()
		buttonSubmit0.setId(0);
		componentChildren.addChildAtOrdinal(buttonSubmit0, Child.ORDINAL_LAST)
	
		ButtonSubmit buttonSubmit1 = new ButtonSubmit()
		buttonSubmit1.setId(1);
		componentChildren.addChildAtOrdinal(buttonSubmit1, Child.ORDINAL_LAST)
		
		ButtonSubmit buttonSubmit2 = new ButtonSubmit()
		buttonSubmit2.setId(2);
		componentChildren.addChildAtOrdinal(buttonSubmit2, 0)
		
		ImmutableList<Child> children = componentChildren.getList()
			
		when:
		componentChildren.sortByOrdinal()
		
		then:
		Child child0 = children.get(0)
		child0.ordinal == 0
		child0.getId() == 2
		Child child1 = children.get(1)
		child1.ordinal == 1
		child1.getId() == 0
		Child child2 = children.get(2)
		child2.ordinal == 2
		child2.getId() == 1
	}
}
