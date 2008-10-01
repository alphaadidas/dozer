/*
 * Copyright 2005-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.dozer.functional_tests;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.dozer.util.mapping.DozerBeanMapper;
import net.sf.dozer.util.mapping.vo.A;
import net.sf.dozer.util.mapping.vo.Aliases;
import net.sf.dozer.util.mapping.vo.B;
import net.sf.dozer.util.mapping.vo.C;
import net.sf.dozer.util.mapping.vo.D;
import net.sf.dozer.util.mapping.vo.FieldValue;
import net.sf.dozer.util.mapping.vo.FlatIndividual;
import net.sf.dozer.util.mapping.vo.Individual;
import net.sf.dozer.util.mapping.vo.Individuals;
import net.sf.dozer.util.mapping.vo.index.Mccoy;
import net.sf.dozer.util.mapping.vo.index.MccoyPrime;

/**
 * @author wojtek.kiersztyn
 * @author dominic.peciuch
 * 
 */
public class IndexMappingTest extends AbstractMapperTest {

  protected void setUp() {
    mapper = getMapper(new String[] { "IndividualMapping.xml" });
  }

  public void testMap1() throws Exception {
    List userNames = (List) newInstance(ArrayList.class);
    userNames.add("username1");
    userNames.add("username2");

    String[] secondNames = new String[3];
    secondNames[0] = "secondName1";
    secondNames[1] = "secondName2";
    secondNames[2] = "secondName3";

    Individuals source = (Individuals) newInstance(Individuals.class);
    source.setUsernames(userNames);
    source.setSimpleField("a very simple field");
    source.setSecondNames(secondNames);

    Set mySet = (Set) newInstance(HashSet.class);
    mySet.add("myString");

    source.setAddressSet(mySet);

    FlatIndividual dest = (FlatIndividual) mapper.map(source, FlatIndividual.class);

    assertEquals(source.getUsernames().get(0), dest.getUsername1());
    assertEquals(source.getSimpleField(), dest.getSimpleField());
    assertEquals(source.getSecondNames()[1], dest.getSecondName1());
    assertEquals(source.getSecondNames()[2], dest.getSecondName2());
    assertEquals("myString", dest.getAddress());
  }

  public void testMap1Inv() {
    FlatIndividual source = (FlatIndividual) newInstance(FlatIndividual.class);
    source.setUsername1("username1");
    source.setUsername2("username2");
    source.setSimpleField("a simple field");
    source.setSecondName1("secondName1");
    source.setSecondName2("secondName2");
    source.setPrimaryAlias("aqqq");
    source.setThirdName("thirdName");

    Individuals dest = (Individuals) mapper.map(source, Individuals.class);

    assertEquals(source.getUsername1(), dest.getUsernames().get(0));
    assertEquals(dest.getIndividual().getUsername(), source.getUsername2());
    assertEquals(dest.getAliases().getOtherAliases()[0], "aqqq");
    assertEquals(source.getUsername2(), dest.getUsernames().get(1));
    assertEquals(source.getSimpleField(), dest.getSimpleField());
    assertEquals(source.getSecondName1(), dest.getSecondNames()[1]);
    assertEquals(source.getSecondName2(), dest.getSecondNames()[2]);
    assertEquals(source.getThirdName(), dest.getThirdNameElement1());
  }

  public void testMap3() {
    List userNames = (List) newInstance(ArrayList.class);
    userNames.add("username1");
    userNames.add("username2");

    Individual nestedIndividual = (Individual) newInstance(Individual.class);
    nestedIndividual.setUsername("nestedusername");

    String[] secondNames = new String[3];
    secondNames[0] = "secondName1";
    secondNames[1] = "secondName2";
    secondNames[2] = "secondName3";

    Individuals source = (Individuals) newInstance(Individuals.class);
    source.setUsernames(userNames);
    source.setIndividual(nestedIndividual);
    source.setSecondNames(secondNames);

    FlatIndividual dest = (FlatIndividual) mapper.map(source, FlatIndividual.class);

    assertEquals(source.getUsernames().get(0), dest.getUsername1());
    assertEquals(source.getIndividual().getUsername(), dest.getUsername2());
    assertEquals(source.getSecondNames()[1], dest.getSecondName1());
    assertEquals(source.getSecondNames()[2], dest.getSecondName2());
  }

  public void testNulls() {
    FlatIndividual source = (FlatIndividual) newInstance(FlatIndividual.class);
    source.setSimpleField("a simplefield");

    Individuals dest = (Individuals) mapper.map(source, Individuals.class);
    assertEquals(source.getSimpleField(), dest.getSimpleField());
  }

  public void testNullsInv() {
    Individuals source = (Individuals) newInstance(Individuals.class);
    source.setSimpleField("a simplefield");

    FlatIndividual dest = (FlatIndividual) mapper.map(source, FlatIndividual.class);
    assertEquals(source.getSimpleField(), dest.getSimpleField());
  }

  public void testNestedArray() {
    Individuals source = (Individuals) newInstance(Individuals.class);
    Aliases aliases = (Aliases) newInstance(Aliases.class);
    aliases.setOtherAliases(new String[] { "other alias 1", "other alias 2" });
    source.setAliases(aliases);

    FlatIndividual dest = (FlatIndividual) mapper.map(source, FlatIndividual.class);
    assertEquals("other alias 1", dest.getPrimaryAlias());
  }

  public void testNotNullNestedIndexAtoD() {
    C c = (C) newInstance(C.class);
    c.setValue("value1");
    B b = (B) newInstance(B.class);
    b.setCs(new C[] { c });
    A a = (A) newInstance(A.class);
    a.setB(b);

    D d = (D) mapper.map(a, D.class);
    assertEquals("value not translated", "value1", d.getValue());
  }

  public void testNullNestedIndexAtoD() {
    A a = (A) newInstance(A.class);

    D d = (D) mapper.map(a, D.class);
    assertNull("value should not be translated", d.getValue());
  }

  public void testNotNullNestedIndexDtoA() {
    D d = (D) newInstance(D.class);
    d.setValue("value1");

    A a = (A) mapper.map(d, A.class);
    assertEquals("value not translated", d.getValue(), a.getB().getCs()[0].getValue());
  }

  public void testNullNestedIndexDtoA() {
    D d = (D) newInstance(D.class);
    A a = (A) mapper.map(d, A.class);
    assertNotNull(a);
  }

  public void testStringToIndexedSet_UsingMapSetMethod() {
    mapper = (DozerBeanMapper) getMapper(new String[] { "indexMapping.xml" });
    Mccoy src = (Mccoy) newInstance(Mccoy.class);
    src.setStringProperty(String.valueOf(System.currentTimeMillis()));

    MccoyPrime dest = (MccoyPrime) mapper.map(src, MccoyPrime.class);
    Set destSet = dest.getFieldValueObjects();
    assertNotNull("dest set should not be null", destSet);
    assertEquals("dest set should contain 1 entry", 1, destSet.size());
    Object entry = destSet.iterator().next();
    assertTrue("dest set entry should be instance of FieldValue", entry instanceof FieldValue);
    assertEquals("invalid value for dest object", src.getStringProperty(), ((FieldValue) entry).getValue("stringProperty"));
  }

  protected DataObjectInstantiator getDataObjectInstantiator() {
    return NoProxyDataObjectInstantiator.INSTANCE;
  }

}