/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author JLiu
 */
public class MeasureExecutionTimes {

	protected static final int NUM_MEASUREMENTS = 12;

	protected static long measure(
			VGI inVgi,
			String inFilePath,
			String inSourceName,
			String inTargetName,
			boolean inAddTransition,
			boolean inApplyRES) {

		inVgi.openFile(inFilePath);
		JgraphXInternalFrame frame = inVgi.getSelectedFrame();
		mxGraph graph = frame.graph;
		Object parent = graph.getDefaultParent();
		Object objects[] = graph.getChildVertices(parent);
		mxCell source = null;
		mxCell target = null;
		for (Object object : objects) {
			if (!(object instanceof mxCell)) {
				throw new IllegalStateException("A vertex is not of the type mxCell.");
			}
			mxCell vertex = (mxCell) object;
			if (!(vertex.isVertex())) {
				throw new IllegalStateException("The 'vertex' variable is not a vertex.");
			}
			Object value = vertex.getValue();
			if (!(value instanceof String)) {
				continue;
			}
			String string = (String) value;
			if (string.equals(inSourceName)) {
				source = vertex;
			} else if (string.equals(inTargetName)) {
				target = vertex;
			}
			if ((source != null) && (target != null)) {
				break;
			}
		}  // End for (Object object : objects)
		if ((source == null) || (target == null)) {
			throw new IllegalStateException("Missing source or target vertex.");
		}
		if (inAddTransition) {
			frame.addTransition(source, target);
		}
		mxCell edgeToRoute = null;
		objects = graph.getChildEdges(parent);
		for (Object object : objects) {
			if (!(object instanceof mxCell)) {
				throw new IllegalStateException("An edge is not of the type mxCell.");
			}
			mxCell edge = (mxCell) object;
			if (!(edge.isEdge())) {
				throw new IllegalStateException("The 'edge' variable is not an edge.");
			}
			if ((edge.getTerminal(true) == source)
					&& (edge.getTerminal(false) == target)) {
				edgeToRoute = edge;
				break;
			}
		}  // End for (Object object : objects)
		if (edgeToRoute == null) {
			throw new IllegalStateException("Missing the edge to route.");
		}
		EdgeRoutingMinCross edgeRoutingMinCross = new EdgeRoutingMinCross(graph);
		Stopwatch stopwatch = new Stopwatch();
		if (inApplyRES) {
			stopwatch.start();
			edgeRoutingMinCross.route(edgeToRoute);
			stopwatch.stop();
		} else {
			stopwatch.start();
			edgeRoutingMinCross.routeByWeightedVisibilityGraph(edgeToRoute);
			stopwatch.stop();
		}
		frame.modified = false;
		inVgi.closeSelectedFrame();

		return Math.round(stopwatch.getElapsedMilliseconds());
	}  // End protected static long measure(...)

	protected static void logRun(
			VGI inVgi,
			String inFilePathWithoutExtension,
			String inSourceName,
			String inTargetName,
			boolean inAddTransition,
			int inTimesToRepeat) {

		PrintStream printStream = null;
		try {
			printStream = new PrintStream(new File(inFilePathWithoutExtension + ".log"));
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
		}

		List<Long> dataEntries = new LinkedList();

		for (int index = 0; index < inTimesToRepeat; index++) {
			try {
				Long currentDuration = MeasureExecutionTimes.measure(
						inVgi,
						inFilePathWithoutExtension + ".xml",
						inSourceName,
						inTargetName,
						inAddTransition,
						false);
				dataEntries.add(currentDuration);
			} catch (Exception exception) {
				exception.printStackTrace();
				dataEntries = null;  // List<Long> dataEntries = new LinkedList();
				printStream.close();
				return;
			}
		}  // End for (int index = 0; index < inTimesToRepeat; index++)

		Long minimum = Long.MAX_VALUE;
		Long maximum = Long.MIN_VALUE;

		for (Long currentDuration : dataEntries) {
			if (currentDuration < minimum) {
				minimum = currentDuration;
			}
			if (currentDuration > maximum) {
				maximum = currentDuration;
			}
		}  // End for (Long currentDuration : dataEntries)

		printStream.println("Weighted Visibility Graph Execution Time in ms:");
		long totalDuration = 0;

		for (Long currentDuration : dataEntries) {
			if (currentDuration == minimum) {
				printStream.println(currentDuration + " min");
			} else if (currentDuration == maximum) {
				printStream.println(currentDuration + " max");
			} else {
				printStream.println(currentDuration);
				totalDuration = totalDuration + currentDuration.longValue();
			}
		}  // End for (Long currentDuration : dataEntries)

		printStream.println("Average:  " + Math.round(((double) totalDuration) / (inTimesToRepeat - 2)));

		dataEntries.clear();

		for (int index = 0; index < inTimesToRepeat; index++) {
			try {
				Long currentDuration = MeasureExecutionTimes.measure(
						inVgi,
						inFilePathWithoutExtension + ".xml",
						inSourceName,
						inTargetName,
						inAddTransition,
						true);
				dataEntries.add(currentDuration);
			} catch (Exception exception) {
				exception.printStackTrace();
				dataEntries = null;  // List<Long> dataEntries = new LinkedList();
				printStream.close();
				return;
			}
		}  // End for (int index = 0; index < inTimesToRepeat; index++)

		minimum = Long.MAX_VALUE;
		maximum = Long.MIN_VALUE;

		for (Long currentDuration : dataEntries) {
			if (currentDuration < minimum) {
				minimum = currentDuration;
			}
			if (currentDuration > maximum) {
				maximum = currentDuration;
			}
		}  // End for (Long currentDuration : dataEntries)

		printStream.println("Repeatedly Expanding Subgraph Execution Time in ms:");
		totalDuration = 0;

		for (Long currentDuration : dataEntries) {
			if (currentDuration == minimum) {
				printStream.println(currentDuration + " min");
			} else if (currentDuration == maximum) {
				printStream.println(currentDuration + " max");
			} else {
				printStream.println(currentDuration);
				totalDuration = totalDuration + currentDuration.longValue();
			}
		}  // End for (Long currentDuration : dataEntries)

		printStream.println("Average:  " + Math.round(((double) totalDuration) / (inTimesToRepeat - 2)));

		dataEntries = null;  // List<Long> dataEntries = new LinkedList();
		printStream.close();
	}  // End protected static void logRun(...)

	public static void main(String args[]) {
		VGI vgi = new VGI();
//		vgi.setVisible(true);
		MeasureExecutionTimes.logRun(vgi, "test input/char-b/1 crossing max", "s0", "s2", true, 5);
//		MeasureExecutionTimes.logRun(vgi, "test input/char-b/1 crossing max", "s0", "s2", true, NUM_MEASUREMENTS);
//		MeasureExecutionTimes.logRun(vgi, "test input/char-b/cycle test", "s4", "s1", false, NUM_MEASUREMENTS);
//		MeasureExecutionTimes.logRun(vgi, "test input/char-b/5x5mesh", "s6", "s18", true, NUM_MEASUREMENTS);
//		MeasureExecutionTimes.logRun(vgi, "test input/char-b/5x5", "s6", "s18", true, NUM_MEASUREMENTS);
		vgi.exitProgram();
	}  // End public static void main(String args[])
}  // End public class MeasureExecutionTimes