/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    SwapValues.java
 *    Copyright (C) 1999 Eibe Frank
 *
 */


package weka.filters.unsupervised.attribute;

import weka.filters.*;
import java.io.*;
import java.util.*;
import weka.core.*;


/** 
 * Swaps two values of a nominal attribute.<p>
 *
 * Valid filter-specific options are: <p>
 *
 * -C col <br>
 * Index of the attribute to be changed. (default last)<p>
 *
 * -F index <br>
 * Index of the first value (default first).<p>
 *
 * -S index <br>
 * Index of the second value (default last).<p>
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz) 
 * @version $Revision: 1.1 $
 */
public class SwapValues extends Filter 
  implements UnsupervisedFilter, StreamableFilter, OptionHandler {

  /** The attribute's index setting. */
  private SingleIndex m_AttIndex = new SingleIndex("last"); 

  /** The first value's index setting. */
  private SingleIndex m_FirstIndex = new SingleIndex("first");

  /** The second value's index setting. */
  private SingleIndex m_SecondIndex = new SingleIndex("last");

  /**
   * Returns a string describing this filter
   *
   * @return a description of the filter suitable for
   * displaying in the explorer/experimenter gui
   */
  public String globalInfo() {

    return  "Swaps two values of a nominal attribute.";
  }

  /**
   * Sets the format of the input instances.
   *
   * @param instanceInfo an Instances object containing the input 
   * instance structure (any instances contained in the object are 
   * ignored - only the structure is required).
   * @return true if the outputFormat may be collected immediately
   * @exception UnsupportedAttributeTypeException if the selected attribute
   * is not nominal or if it only has one value.
   * @exception Exception if the input format can't be set 
   * successfully
   */
  public boolean setInputFormat(Instances instanceInfo) 
       throws Exception {

    super.setInputFormat(instanceInfo);
    m_AttIndex.setUpper(instanceInfo.numAttributes() - 1);
    m_FirstIndex.setUpper(instanceInfo.
			  attribute(m_AttIndex.getIndex()).numValues() - 1);
    m_SecondIndex.setUpper(instanceInfo.
			   attribute(m_AttIndex.getIndex()).numValues() - 1);
    if (!instanceInfo.attribute(m_AttIndex.getIndex()).isNominal()) {
      throw new UnsupportedAttributeTypeException("Chosen attribute not nominal.");
    }
    if (instanceInfo.attribute(m_AttIndex.getIndex()).numValues() < 2) {
      throw new UnsupportedAttributeTypeException("Chosen attribute has less than " 
						  + "two values.");
    }
    setOutputFormat();
    return true;
  }

  /**
   * Input an instance for filtering. The instance is processed
   * and made available for output immediately.
   *
   * @param instance the input instance
   * @return true if the filtered instance may now be
   * collected with output().
   * @exception IllegalStateException if no input structure has been defined.
   */
  public boolean input(Instance instance) {

    if (getInputFormat() == null) {
      throw new IllegalStateException("No input instance format defined");
    }
    if (m_NewBatch) {
      resetQueue();
      m_NewBatch = false;
    }
    Instance newInstance = (Instance)instance.copy();
    if (!newInstance.isMissing(m_AttIndex.getIndex())) {
      if ((int)newInstance.value(m_AttIndex.getIndex()) == m_SecondIndex.getIndex()) {
        newInstance.setValue(m_AttIndex.getIndex(), (double)m_FirstIndex.getIndex());
      } else if ((int)newInstance.value(m_AttIndex.getIndex()) == 
		 m_FirstIndex.getIndex()) {
        newInstance.setValue(m_AttIndex.getIndex(), (double)m_SecondIndex.getIndex());
      }
    }
    push(newInstance);
    return true;
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  public Enumeration listOptions() {

    Vector newVector = new Vector(3);

    newVector.addElement(new Option(
              "\tSets the attribute index (default last).",
              "C", 1, "-C <col>"));

    newVector.addElement(new Option(
              "\tSets the first value's index (default first).",
              "F", 1, "-F <value index>"));

    newVector.addElement(new Option(
              "\tSets the second value's index (default last).",
              "S", 1, "-S <value index>"));

    return newVector.elements();
  }


  /**
   * Parses the options for this object. Valid options are: <p>
   *
   * -C col <br>
   * The column containing the values to be merged. (default last)<p>
   *
   * -F index <br>
   * Index of the first value (default first).<p>
   *
   * -S index <br>
   * Index of the second value (default last).<p>
   *
   * @param options the list of options as an array of strings
   * @exception Exception if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    
    String attIndex = Utils.getOption('C', options);
    if (attIndex.length() != 0) {
      setAttributeIndex(attIndex);
    } else {
      setAttributeIndex("last");
    }

    String firstValIndex = Utils.getOption('F', options);
    if (firstValIndex.length() != 0) {
      setFirstValueIndex(firstValIndex);
    } else {
      setFirstValueIndex("first");
    }

    String secondValIndex = Utils.getOption('S', options);
    if (secondValIndex.length() != 0) {
      setSecondValueIndex(secondValIndex);
    } else {
      setSecondValueIndex("last");
    }
   
    if (getInputFormat() != null) {
      setInputFormat(getInputFormat());
    }
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {

    String [] options = new String [6];
    int current = 0;

    options[current++] = "-C";
    options[current++] = "" + (getAttributeIndex());
    options[current++] = "-F"; 
    options[current++] = "" + (getFirstValueIndex());
    options[current++] = "-S"; 
    options[current++] = "" + (getSecondValueIndex());
    while (current < options.length) {
      options[current++] = "";
    }
    return options;
  }

  /**
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String attributeIndexTipText() {

    return "Sets which attribute to process. This "
      + "attribute must be nominal (\"first\" and \"last\" are valid values)";
  }

  /**
   * Get the index of the attribute used.
   *
   * @return the index of the attribute
   */
  public String getAttributeIndex() {

    return m_AttIndex.getSingleIndex();
  }

  /**
   * Sets index of the attribute used.
   *
   * @param index the index of the attribute
   */
  public void setAttributeIndex(String attIndex) {
    
    m_AttIndex.setSingleIndex(attIndex);
  }

  /**
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String firstValueIndexTipText() {

    return "The index of the first value."
      + "(\"first\" and \"last\" are valid values)";
  }

  /**
   * Get the index of the first value used.
   *
   * @return the index of the first value
   */
  public String getFirstValueIndex() {

    return m_FirstIndex.getSingleIndex();
  }

  /**
   * Sets index of the first value used.
   *
   * @param index the index of the first value
   */
  public void setFirstValueIndex(String firstIndex) {
    
    m_FirstIndex.setSingleIndex(firstIndex);
  }

  /**
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String secondValueIndexTipText() {

    return "The index of the second value."
      + "(\"first\" and \"last\" are valid values)";
  }

  /**
   * Get the index of the second value used.
   *
   * @return the index of the second value
   */
  public String getSecondValueIndex() {

    return m_SecondIndex.getSingleIndex();
  }

  /**
   * Sets index of the second value used.
   *
   * @param index the index of the second value
   */
  public void setSecondValueIndex(String secondIndex) {
    
    m_SecondIndex.setSingleIndex(secondIndex);
  }

  /**
   * Set the output format. Swapss the desired nominal attribute values in
   * the header and calls setOutputFormat(Instances) appropriately.
   */
  private void setOutputFormat() {
    
    Instances newData;
    FastVector newAtts, newVals;
      
    // Compute new attributes
      
    newAtts = new FastVector(getInputFormat().numAttributes());
    for (int j = 0; j < getInputFormat().numAttributes(); j++) {
      Attribute att = getInputFormat().attribute(j);
      if (j != m_AttIndex.getIndex()) {
	newAtts.addElement(att.copy()); 
      } else {
	  
	// Compute list of attribute values
	  
	newVals = new FastVector(att.numValues());
	for (int i = 0; i < att.numValues(); i++) {
	  if (i == m_FirstIndex.getIndex()) {
	    newVals.addElement(att.value(m_SecondIndex.getIndex()));
	  } else if (i == m_SecondIndex.getIndex()) {
	    newVals.addElement(att.value(m_FirstIndex.getIndex()));
	  } else {
	    newVals.addElement(att.value(i)); 
	  }
	}
	newAtts.addElement(new Attribute(att.name(), newVals));
      }
    }
      
    // Construct new header
      
    newData = new Instances(getInputFormat().relationName(), newAtts, 0);
    newData.setClassIndex(getInputFormat().classIndex());
    setOutputFormat(newData);
  }
  
  /**
   * Main method for testing this class.
   *
   * @param argv should contain arguments to the filter: 
   * use -h for help
   */
  public static void main(String [] argv) {

    try {
      if (Utils.getFlag('b', argv)) {
 	Filter.batchFilterFile(new SwapValues(), argv);
      } else {
	Filter.filterFile(new SwapValues(), argv);
      }
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
  }
}







