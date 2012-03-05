package vgi;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;

public class Automata implements AutomataInterface {

	@Override
	public String getName() {
		return this.pmName;
	}

	@Override
	public void setName(String name) {
		this.pmName = name;
	}

	/**
	 * @return the Type
	 */
	@Override
	public WritingData getWritingData() {
		return this.pmWritingData;
	}

	@Override
	public void setWritingData(WritingData writingData) {
		this.pmWritingData = writingData;
	}

	@Override
	public Weight getWeight() {
		return this.pmWeight;
	}

	@Override
	public void setWeight(Weight weight) {
		this.pmWeight = weight;
	}

	@Override
	public Alphabet getAlphabet() {
		return this.pmAlphabet;
	}

	@Override
	public void setAlphabet(Alphabet alphabet) {
		this.pmAlphabet = alphabet;
	}

	@Override
	public Alphabet getOutputAlphabet() {
		return this.pmOutputAlphabet;
	}

	@Override
	public void setOutputAlphabet(Alphabet alphabet) {
		this.pmOutputAlphabet = alphabet;
	}

	/**
	 * @return the states
	 */
	@Override
	public List<State> getAllStates() {
		return this.pmAllStates;
	}

	/**
	 * @param states the states to set
	 */
	@Override
	public void setAllStates(List<State> allStates) {
		this.pmAllStates = allStates;
	}

	@Override
	public void addState(State state) {
		pmAllStates.add(state);
	}

	/**
	 * @return the transitions
	 */
	@Override
	public List<Transition> getAllTransitions() {
		return this.pmAllTransitions;
	}

	/**
	 * @param transitions the transitions to set
	 */
	@Override
	public void setAllTransitions(List<Transition> transitions) {
		this.pmAllTransitions = transitions;
	}

	@Override
	public void addTransition(Transition transition) {
		pmAllTransitions.add(transition);
		transition.getSourceState().addTransition(transition);
		transition.getTargetState().addTransition(transition);
	}
	private String pmName;
	private WritingData pmWritingData;
	private Weight pmWeight;
	private Alphabet pmAlphabet;
	private Alphabet pmOutputAlphabet;
	private List<State> pmAllStates;
	private List<Transition> pmAllTransitions;

	public Automata() {
		this.pmName = null;
		this.pmWritingData = null;
		this.pmWeight = null;
		this.pmAlphabet = null;
		this.pmOutputAlphabet = null;
		this.pmAllStates = new ArrayList<State>();
		this.pmAllTransitions = new ArrayList<Transition>();
	}

	public Automata(Weight weight) {
		this();
		this.pmWeight = weight;
	}

	public static void main(String args[]) {
		System.out.println("Staring from 'Automata Class'");
		System.out.println("Creating Weight");
		Weight weight = new Weight();
		System.out.println("Creating empty Automata");
		Automata automata = new Automata();
		System.out.println("Creating Automata with Weight");
		Automata automataWithWeight = new Automata(weight);
	}
}  // End public class Automata implements AutomataInterface
