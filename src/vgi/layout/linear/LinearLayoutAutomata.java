/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.layout.linear;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.*;
import vgi.automata.*;
import vgi.layout.helperclass.GroupReplacedAutomata;
import vgi.layout.helperclass.SccDfs;

/**
 *
 * @author reng
 */
public class LinearLayoutAutomata {
    
    Automata automata;
    double lineY;
    double upboundY=lineY;
    
    Rectangle bound;
    
    List<State> allVertexList;
    int vertexNum;
    
    List<State> vertexMapList;
    List<Transition> addedTopEdge;
    List<Transition> addedBottomEdge;
    
    List<State> upPhaseList;
    List<State> downPhaseList;
    
    
    ////// if there's fixed vertex groups
    boolean useVertexGroup=false;
    
    /////
    
    public LinearLayoutAutomata(){}
    
    public void doLayout(Automata automata_){
        automata=automata_;
//        vertexNum=automata.getAllStates().size();
//        allVertexList=getAllVertices(automata_);

        allVertexList=getSelectedVertices(automata_);
        vertexNum=allVertexList.size();
        
        bound=automata.computeBox(allVertexList);
        System.out.println("box: "+bound);
        
        lineY=bound.getCenterY();
        if(bound.x<0) bound.x=0;
        if(allVertexList.size()==automata.getAllStates().size()){
            lineY=0;
            bound.x=-bound.width/2;
        }
        upboundY=bound.getMaxY(); // ???
        
        System.out.println("lineY= "+lineY);
        
        //graph.getModel().beginUpdate();
        //    try{
            
            
            SccDfs sccdfs=new SccDfs(allVertexList);
            vertexMapList=sccdfs.getExpandedListWithSCC();
                    
            //vertexMapList=this.sortVertices(automata_);
            setSortedVerticesLocation();
            
//            decideEdgesGoUpDown();
//            setEdgesCurveHeight();
          
        adjustEdgeCurves(true);
            
        if(upboundY>bound.getMaxY()) moveWholeBound();
            
       // }finally{
           // graph.getModel().endUpdate();
       // }
    }
    
    // if there is vertexGroup
    public void doLayout(Automata automata_,GroupReplacedAutomata replaceAutomata){
        
        Automata reautomata=replaceAutomata.createReplaceAutomata();
        reautomata.selectAllStates();
        automata=reautomata;

        allVertexList=getSelectedVertices(automata_);
        vertexNum=allVertexList.size();
        
        bound=automata.computeBox(allVertexList);
        System.out.println("box: "+bound);
        
        lineY=bound.getCenterY();
        
        if(allVertexList.size()==automata.getAllStates().size()){
            lineY=0;
            bound.x=-bound.width/2;
        }
        upboundY=lineY; // ???
        System.out.println("lineY= "+lineY);
        
        SccDfs sccdfs=new SccDfs(allVertexList);
        vertexMapList=sccdfs.getExpandedListWithSCC();
        setSortedVerticesLocation();
            
//        replaceAutomata.expandStatesToVertexGroups();
        vertexMapList=replaceAutomata.expandStatesToLinearLayout(vertexMapList);
        vertexNum=vertexMapList.size();
        
        automata=automata_;
        
//        decideEdgesGoUpDown();
//        setEdgesCurveHeight();
        adjustEdgeCurves(true);  
        
        if(upboundY>bound.getMaxY()) moveWholeBound();
        
    }
    
    private List<State> getSelectedVertices(Automata automata_){

        //vertexNum=automata.getAllStates().size();
//        return automata.getAllStates();
        
          // only deal with selected states
          
          return automata.getSelectedStates();
    }

    
    private void setSortedVerticesLocation(){
         double x=bound.x;
         if(x<0) x=0;
//         double distance=bound.getWidth()/vertexNum;
         double distance=0;
         
         for(int i = 0; i<vertexNum; ++i){
                    //mxCell cell=vertexMapList.get(i);
                    //mxGeometry geo=cell.getGeometry();
                    State state=vertexMapList.get(i);
                    //StateGeometricData geodata=state.getGeometricData();
                    StateGeometricData geodata=automata.getStateGeometricData(state);
                    
                    //double distance=geodata.getWidth()/2;
                    
//                    if(i>0){
//                        State lastState=vertexMapList.get(i-1);
////                        distance+=lastState.getGeometricData().getWidth();
//                        distance+=automata.getStateGeometricData(lastState).getWidth()/2;
//                    }else{
//                        State nextState=vertexMapList.get(i+1);
//                        distance+=automata.getStateGeometricData(nextState).getWidth()/2;
//                    }
                          
                    
                    //setVertexLocation(state,x+distance*0.4,lineY-geodata.getHeight()/2);
//                    automata.moveState(state, new Point2D.Double(x+distance*0.3,lineY));
                    geodata.setLocation(new Point2D.Double(x+geodata.getWidth()/2+(distance+geodata.getWidth()/2)*0.3,lineY));
                    automata.setStateGeometricData(state, geodata);
                    //x=state.getGeometricData().getX()+state.getGeometricData().getWidth();
                    geodata=automata.getStateGeometricData(state);
                    x=geodata.getX()+geodata.getWidth()/2;
                    distance=geodata.getWidth()/2;
                    System.out.println("set location: "+state.getName()+" "+geodata.getX()+","+geodata.getY());
                    
         }
    }
    
    public void adjustEdgeCurves(boolean ignoreStateInGroups){
            decideEdgesGoUpDown(ignoreStateInGroups);
            setEdgesCurveHeight(ignoreStateInGroups);
        
    }
    public void adjustEdgeCurves(Automata automata_,List<State> vertexList){
        automata=automata_;
        vertexMapList=vertexList; 
        vertexNum=vertexMapList.size();
        System.out.println("adjust linear layout edges: "+vertexMapList);
        
        bound=automata.computeBox(vertexMapList);
        System.out.println("box: "+bound);
        
        lineY=0;
        for(State s:vertexList){
            lineY+=s.getGeometricData().getY();
        }
        lineY/=vertexNum;
        
        // sort state order in vertexList by x-coordinates
        Collections.sort(vertexList, new Comparator<State>(){
                        @Override
                        public int compare(State s1, State s2) {
                            Double position1=s1.getGeometricData().getX();
                            Double position2=s2.getGeometricData().getX();
                            return position1.compareTo(position2);
                        }
                            
                    });
                
        
        decideEdgesGoUpDown(false);
        setEdgesCurveHeight(false);
        
    }
    /*
     *  decide whether an edge goes up or down, choose the one with fewer crossings
     *  
     *  addedTopEdge,addedBottomEdge are updated
     */
    private void decideEdgesGoUpDown(boolean ignoreStateInGroups){
            addedTopEdge=new ArrayList<Transition>();
            addedBottomEdge=new ArrayList<Transition>();
            
            upPhaseList=new ArrayList<State>();
            downPhaseList=new ArrayList<State>();
            
            // if ignore state in groups, first put edges in groups into top/bottom list
//            if(ignoreStateInGroups){
                 for(int i = 0; i<vertexNum; ++i){
                    State state=vertexMapList.get(i);
                    
                    
//                    System.out.println(state+" y= "+state.getGeometricData().getY());
                    int phase=0;
                    if(state.getGeometricData().getY()>lineY){
                        phase=1;
                        upPhaseList.add(state);
                    }
                    else if(state.getGeometricData().getY()<lineY){
                        phase=-1;
                        downPhaseList.add(state);
                    }else{
                        upPhaseList.add(state);
                        downPhaseList.add(state);
                    }
                    
                    if(ignoreStateInGroups){
                        int id=state.getGroupID();
                        if(id==-1) continue;



                        List<Transition> outGoingEdges=state.getTransitions();
                        int edgeCount=outGoingEdges.size();
                        for(int j=0;j<edgeCount;++j){
                            Transition edge=outGoingEdges.get(j);


                            State target=edge.getTargetState();
                            if(target==state) target=edge.getSourceState();
                            if(target==state) continue; //loop


                            // if the other terminal is not in the same group
                            if(target.getGroupID()!=id){
                                // if one terminal point is above/below horizontal line, add to top/bottom directly.
                                int phaset=0;
                                if(target.getGeometricData().getY()>lineY) phaset=1;
                                else if(target.getGeometricData().getY()<lineY) phaset=-1;

                                if(phase>0 || phaset>0){
                                    addedTopEdge.add(edge);
                                }else if(phase<0 || phaset<0){
                                    addedBottomEdge.add(edge);
                                }

                            }else{ // ingroup edges
                                List<Point2D> controlPoints=edge.getGeometricData().controlPoints;
                                System.out.println("edge in group :control points: "+controlPoints);
                                if(controlPoints.size()>0){
                                    Point2D firstPoint=controlPoints.get(0);
                                    if(firstPoint.getY()>lineY){ addedTopEdge.add(edge);continue;}
                                    else{ addedBottomEdge.add(edge);continue;}
                                }
                            }
                        }
    //                         // if one terminal point is above/below horizontal line, add to top/bottom directly.
//                         int phaset=0;
//                         if(target.getGeometricData().getY()>lineY) phaset=1;
//                         else if(target.getGeometricData().getY()<lineY) phaset=-1;
//                         
//                         
//                         // if in the same group
//                        if(target.getGroupID()==groupId){
//                            
//                            List<Point2D> controlPoints=edge.getGeometricData().controlPoints;
//                            System.out.println("edge in group :control points: "+controlPoints);
//                            if(controlPoints.size()>0){
//                                Point2D firstPoint=controlPoints.get(0);
//                                if(firstPoint.getY()>lineY){ addedTopEdge.add(edge);continue;}
//                                else{ addedBottomEdge.add(edge);continue;}
//                            }
//                         }else{
//                            if(phase>0 && phaset>0){
//                                addedTopEdge.add(edge);
//                            }else if(phase<0 && phaset<0){
//                                addedBottomEdge.add(edge);
//                            }
//                            
//                         }
                    }
                 }
                            
//           }
            
            for(int i = 0; i<vertexNum; ++i){
                    State state=vertexMapList.get(i);
                    
                        
                    // add edges following the sorted vertex order
                    // determine whether an edge goes from top or bottom
//                    Object[] outGoingEdges=graph.getOutgoingEdges(cell);
//                    int edgeCount=outGoingEdges.length;
//                    for(int j=0;j<edgeCount;++j){
//                        
//                        mxCell edge=(mxCell)outGoingEdges[j];
//                        mxCell target=(mxCell)edge.getTarget();
//                        
//                        //if(target!=cell && target!=null){
//                        if(target!=null){
//                            if(edgeGoUp(edge)){
//                                addedTopEdge.add(edge);
//                            }else{
//                                addedBottomEdge.add(edge);
//                            }
//                        }
//                    }
                    
                    //Object[] outGoingEdges=graph.getAllEdges(new Object[]{cell});
                    
                    List<Transition> outGoingEdges=state.getOutgoingTransitions();
                    int edgeCount=outGoingEdges.size();
                    for(int j=0;j<edgeCount;++j){
                        
//                        mxCell edge=(mxCell)outGoingEdges[j];
//                        mxCell target=(mxCell)edge.getTarget();
//                        mxCell source=(mxCell)edge.getSource();
//                        
                        Transition edge=outGoingEdges.get(j);
                        
                        
                        State target=edge.getTargetState();
                        
                        
                        // if both terminals of the edge are in vertexGroup, 
                        // add to top/bottom by existing control points!
                        // TODO: problem happens when they are in different vertexGroup??
                        
                        if(addedTopEdge.contains(edge)|| addedBottomEdge.contains(edge)) continue;
                        
                        if(target!=null){
                                int sourceIndex=vertexMapList.indexOf(edge.getSourceState());
                                int targetIndex=vertexMapList.indexOf(edge.getTargetState());
        
                                if(sourceIndex>=0 && targetIndex>=0){
                                    if(edgeGoUp(edge)){
                                        addedTopEdge.add(edge);
                                    }else{
                                        addedBottomEdge.add(edge);
                                    }
                                }
                        }
//                        for(Transition tr:addedTopEdge)
//                            System.out.println("now in top:  "+tr.getSourceState()+"->"+tr.getTargetState());
//                        
//                        for(Transition tr:addedBottomEdge)
//                            System.out.println("now in bottom:  "+tr.getSourceState()+"->"+tr.getTargetState());
                    }
                    
                    // if ignore states in groups, don't need to consider loops
                    if(!ignoreStateInGroups){
                        List<Transition> loopEdges=state.getLoopTransitions();
                        int loopCount=loopEdges.size();
                        for(int j=0;j<loopCount;++j){

    //                        mxCell edge=(mxCell)outGoingEdges[j];
    //                        mxCell target=(mxCell)edge.getTarget();
    //                        mxCell source=(mxCell)edge.getSource();
    //                        
                            Transition edge=loopEdges.get(j);
                                if(edgeGoUp(edge)){
                                    addedTopEdge.add(edge);
                                }else{
                                    addedBottomEdge.add(edge);
                                }
                            //}

                        }
                    }
            }

    }
    /*
     * decide an edge to go up or down, depends on minimum crossings
     * if up return true, down return false
     */
    private boolean edgeGoUp(Transition edge){
        
        
        int sourceIndex=vertexMapList.indexOf(edge.getSourceState());
        int targetIndex=vertexMapList.indexOf(edge.getTargetState());
        
        int a=sourceIndex;
        int b=targetIndex;
        if(targetIndex<sourceIndex){ a=targetIndex; b=sourceIndex;}
        
        System.out.println("crossing of "+edge.getSourceState().getName()+" -> "+edge.getTargetState().getName());
        System.out.println("            "+sourceIndex+" -> "+targetIndex);
        
        
        
        int topCrossing=0,bottomCrossing=0;
        
        // Check if there's a backward edge.
        // Since edges are determined from left to right, check only edges with (target <- source)
        if(sourceIndex>targetIndex && automata.hasBackwardTransition(edge)){
            
            System.out.println(" a backward edge!");
            Transition backedge = null;
            List<Transition> incomedges=edge.getSourceState().getIncomingTransitions();
            for(Transition tran:incomedges){
                if(tran.getSourceState()==edge.getTargetState()){
                    backedge=tran;break;
                }
            }
            if(backedge!=null){
                if(addedTopEdge.contains(backedge))  topCrossing+=3;
                else bottomCrossing+=3;
             }
        }
        
        for(Transition topEdge:addedTopEdge){
            int sourceInd=vertexMapList.indexOf(topEdge.getSourceState());
            int targetInd=vertexMapList.indexOf(topEdge.getTargetState());
            
            int c=sourceInd;
            int d=targetInd;
            if(targetInd<sourceInd){ c=targetInd; d=sourceInd;}
//            System.out.println("check  cross top: "+sourceInd+"->"+targetInd);
            //if(targetInd>targetIndex && targetInd<sourceIndex && sourceInd>sourceIndex) 
            if((c>a && c<b && d>b) ||(d>a && d<b && c<a)){
                topCrossing++;
//                System.out.println("  cross top: "+sourceInd+"->"+targetInd);
            }
        }
        
        for(Transition bottomEdge:addedBottomEdge){
            int sourceInd=vertexMapList.indexOf(bottomEdge.getSourceState());
            int targetInd=vertexMapList.indexOf(bottomEdge.getTargetState());
            
            int c=sourceInd;
            int d=targetInd;
            if(targetInd<sourceInd){ c=targetInd; d=sourceInd;}

//            System.out.println("check  cross bottom: "+sourceInd+"->"+targetInd);
            
            //if(targetInd>targetIndex && targetInd<sourceIndex && sourceInd>sourceIndex) 
            if((c>a && c<b && d>b) ||(d>a && d<b && c<a)){
                bottomCrossing++;
//                System.out.println("cross bottom: "+sourceInd+"->"+targetInd);
            }
        }
        //System.out.println("top "+topCrossing+" / bottom "+bottomCrossing);
        
        automata.resetTransitionControlPoint(edge);
        
        if(bottomCrossing<topCrossing) return false;
        else return true;
        
    }
    
    private void setEdgesCurveHeight(boolean ignoreStateInGroups){
        
        Collections.sort(addedTopEdge, new Comparator<Transition>(){
                    @Override
                    public int compare(Transition t, Transition t1) {
                            Integer sourceIndex=vertexMapList.indexOf(t.getSourceState());
                            Integer targetIndex=vertexMapList.indexOf(t.getTargetState());
                            Integer sourceIndex1=vertexMapList.indexOf(t1.getSourceState());
                            Integer targetIndex1=vertexMapList.indexOf(t1.getTargetState());

                        /* if(sourceIndex.compareTo(sourceIndex1)==0) 
                                return targetIndex.compareTo(targetIndex1)*(-1);
                            else return sourceIndex.compareTo(sourceIndex1)*(-1);*/
                            Integer length=Math.abs(sourceIndex-targetIndex);
                            Integer length1=Math.abs(sourceIndex1-targetIndex1);
                            return length.compareTo(length1);

                    }
                            
              });
            Collections.sort(addedBottomEdge, new Comparator<Transition>(){
                    @Override
                    public int compare(Transition t, Transition t1) {
                            Integer sourceIndex=vertexMapList.indexOf(t.getSourceState());
                            Integer targetIndex=vertexMapList.indexOf(t.getTargetState());
                            Integer sourceIndex1=vertexMapList.indexOf(t1.getSourceState());
                            Integer targetIndex1=vertexMapList.indexOf(t1.getTargetState());

                        /* if(sourceIndex.compareTo(sourceIndex1)==0) 
                                return targetIndex.compareTo(targetIndex1)*(-1);
                            else return sourceIndex.compareTo(sourceIndex1)*(-1);*/
                            Integer length=Math.abs(sourceIndex-targetIndex);
                            Integer length1=Math.abs(sourceIndex1-targetIndex1);
                            return length.compareTo(length1);

                    }
                            
              });
            System.out.println("--------set edges curve height-----");
            for(Transition tr:addedTopEdge)
                            System.out.println("now in top:  "+tr.getSourceState()+"->"+tr.getTargetState());
                        
            for(Transition tr:addedBottomEdge)
                            System.out.println("now in bottom:  "+tr.getSourceState()+"->"+tr.getTargetState());
            System.out.println("upPhase: "+upPhaseList);
            System.out.println("downPhase: "+downPhaseList);
            
            
            int len=addedTopEdge.size();
            for(int i=0;i<len;++i){
                Transition edge=addedTopEdge.get(i);
                
                // if ignore state in groups, only edge with one terminal outside group is considered
                // and reset edge curve height
                if(ignoreStateInGroups){
                    int sid=edge.getSourceState().getGroupID();
                    int tid=edge.getTargetState().getGroupID();
                
                    if(sid!=tid || (sid==-1 && tid==-1))
                        setEdgeCurveHeight(edge,true);
                }else{
                    setEdgeCurveHeight(edge,true);    
                }
            }
             
            len=addedBottomEdge.size();
            for(int i=0;i<len;++i){
                Transition edge=addedBottomEdge.get(i);
                if(ignoreStateInGroups){
                    int sid=edge.getSourceState().getGroupID();
                    int tid=edge.getTargetState().getGroupID();
                
                    if(sid!=tid || (sid==-1 && tid==-1))
                        setEdgeCurveHeight(edge,false);
                }
                else{
                    setEdgeCurveHeight(edge,false);
                }
            }
    }
    /*
     * adjust edges' curve height
     * avoid edges having same source/target from crossing each other
     * 
     * TODO: keep all given y coordinates > 0
     */
    private void setEdgeCurveHeight(Transition edge,boolean isTopEdge){
        
        
        
        List<State> phaseList=(isTopEdge)?upPhaseList:downPhaseList;
                
        if(automata!=null) automata.resetTransitionControlPoint(edge);
        
//        int sourceInd=vertexMapList.indexOf(edge.getSourceState());
//        int targetInd=vertexMapList.indexOf(edge.getTargetState());
        int sourceInd=phaseList.indexOf(edge.getSourceState());
        int targetInd=phaseList.indexOf(edge.getTargetState());
        System.out.println("Height of "+edge.getSourceState()+" "+sourceInd+" -> "+edge.getTargetState()+" "+targetInd);
        
        List<Point2D> points=new ArrayList<Point2D>();
        Point2D controlPoint1=null;
        Point2D controlPoint2=null;
        
        if(sourceInd==-1 || targetInd==-1) return;
        //controlPoint.setX((vertexMapList.get(sourceInd).getGeometry().getCenterX()+vertexMapList.get(targetInd).getGeometry().getCenterX())/2);
        
        if(sourceInd==targetInd){  //loop
            controlPoint1=new Point2D.Double();
//            controlPoint1.setX((vertexMapList.get(sourceInd).getGeometricData().getX()+vertexMapList.get(targetInd).getGeometricData().getX())/2);
//            controlPoint1.setY(edge.getSource().getGeometry().getCenterY()+edge.getSource().getGeometry().getHeight());
                        
          //  controlPoint.setY(height+edge.getSource().getGeometry().getHeight()*0.8);                    
         //   controlPoint.setX(edge.getSource().getGeometry().getCenterX());

//            controlPoint1.setLocation((vertexMapList.get(sourceInd).getGeometricData().getX()+vertexMapList.get(targetInd).getGeometricData().getX())/2,
//                    edge.getSourceState().getGeometricData().getY()+edge.getSourceState().getGeometricData().getHeight());
            
            
//            controlPoint1.setLocation(automata.getStateGeometricData(vertexMapList.get(sourceInd)).getX(),
//                    automata.getStateGeometricData(edge.getSourceState()).getY()+automata.getStateGeometricData(edge.getSourceState()).getHeight());
            controlPoint1.setLocation(edge.getSourceState().getGeometricData().getX(),
                    edge.getSourceState().getGeometricData().getY()+edge.getSourceState().getGeometricData().getHeight());
            
            System.out.println("loop"+controlPoint1);
        
        }else{
         if(Math.abs(sourceInd-targetInd)==1){
             
             
             // if there is edge from target-source
             if(automata.hasBackwardTransition(edge)){
                controlPoint1=new Point2D.Double();
             
//                 controlPoint1.setLocation((automata.getStateGeometricData(vertexMapList.get(sourceInd)).getX()+automata.getStateGeometricData(vertexMapList.get(targetInd)).getX())/2,
//                                        lineY+((sourceInd<targetInd)?5:-5));
                 controlPoint1.setLocation((edge.getSourceState().getGeometricData().getX()+edge.getTargetState().getGeometricData().getX())/2,
                                        lineY+((sourceInd<targetInd)?5:-5));
             
             }
//             else
//                controlPoint1.setLocation((automata.getStateGeometricData(vertexMapList.get(sourceInd)).getX()+automata.getStateGeometricData(vertexMapList.get(targetInd)).getX())/2,
//                                        lineY);
             
         }else{
             
            controlPoint1=new Point2D.Double();
            //controlPoint2=new Point2D.Double();
            
//            controlPoint1.setX((vertexMapList.get(sourceInd).getGeometry().getCenterX()+vertexMapList.get(Math.min(sourceInd, targetInd) +1).getGeometry().getCenterX())/2);
//            controlPoint2.setX((vertexMapList.get(targetInd).getGeometry().getCenterX()+vertexMapList.get(Math.max(sourceInd,targetInd)-1).getGeometry().getCenterX())/2);
    //        controlPoint1.setLocation((automata.getStateGeometricData(vertexMapList.get(Math.min(sourceInd,targetInd))).getX()+automata.getStateGeometricData(vertexMapList.get(Math.min(sourceInd, targetInd)+1)).getX())/2,lineY);
            //controlPoint2.setLocation((automata.getStateGeometricData(vertexMapList.get(Math.max(sourceInd,targetInd))).getX()+automata.getStateGeometricData(vertexMapList.get(Math.max(sourceInd,targetInd)-1)).getX())/2,lineY);
            
//            controlPoint1.setLocation((automata.getStateGeometricData(vertexMapList.get(sourceInd)).getX()+automata.getStateGeometricData(vertexMapList.get(targetInd)).getX())/2, lineY);     
            controlPoint1.setLocation((automata.getStateGeometricData(phaseList.get(sourceInd)).getX()+automata.getStateGeometricData(phaseList.get(targetInd)).getX())/2, lineY);     
            
            int a=sourceInd;
            int b=targetInd;
            if(targetInd>sourceInd){ a=targetInd; b=sourceInd;}

            if(isTopEdge){

                int index=addedTopEdge.indexOf(edge);
                int len=addedTopEdge.size();

                //double height=lineY;
                
                // find the one has largest height between source and target
                int middleInd=(int)((float)(a+b)/2);
                double middleHeight=lineY;
                for(int i=b+1;i<a;++i){
//                    double tmpHeight=vertexMapList.get(i).getGeometricData().getY();
                    double tmpHeight=phaseList.get(i).getGeometricData().getY();
                    
                    if(middleHeight<tmpHeight){ 
                        middleInd=i;
                        middleHeight=tmpHeight;
                    }
                }
                
                
//                middleHeight=automata.getStateGeometricData(vertexMapList.get(middleInd)).getHeight();
                middleHeight=automata.getStateGeometricData(phaseList.get(middleInd)).getHeight();
               
                double heightScale=middleHeight/4;
                
                double height=middleHeight/2+phaseList.get(middleInd).getGeometricData().getY();
                System.out.println("middle "+middleInd+"  "+middleHeight+" "+height);
                double higher=0;
                double tmpCurveWidth=0;
                    
                
                for(int i=0;i<len;++i){
                    
                    Transition tmp=addedTopEdge.get(i);
//                    int tmpSourceInd=vertexMapList.indexOf(tmp.getSourceState());
//                    int tmpTargetInd=vertexMapList.indexOf(tmp.getTargetState());
                    int tmpSourceInd=phaseList.indexOf(tmp.getSourceState());
                    int tmpTargetInd=phaseList.indexOf(tmp.getTargetState());
                    //System.out.println("top child "+tmpSourceInd+" -> "+tmpTargetInd);

                    int c=tmpSourceInd;
                    int d=tmpTargetInd;
                    if(tmpTargetInd>tmpSourceInd){ c=tmpTargetInd; d=tmpSourceInd;}

                    //System.out.println("["+a+" , "+b+"]"+"--["+c+" , "+d+"]");
                    

                    //if(a>=c && b<=d && !(a==c && b==d)){
                    if(a>=c && b<=d && !(a==c && b==d)){
                        if(tmp.getGeometricData().controlPoints.size()>0){
                            double tmpHeight=tmp.getGeometricData().controlPoints.get(0).getY();
                            if(height<tmpHeight){
                                height=tmpHeight; higher++;
//                                tmpCurveWidth=Math.abs(automata.getStateGeometricData(vertexMapList.get(c)).getX()-
//                                        automata.getStateGeometricData(vertexMapList.get(d)).getX());
                                tmpCurveWidth=Math.abs(automata.getStateGeometricData(vertexMapList.get(c)).getX()-
                                        automata.getStateGeometricData(phaseList.get(d)).getX());
                                System.out.println("----tmpheight= "+tmpHeight);
                        
                            }else if(height==tmpHeight){
                                height*=1.15;
                                higher++;
                                tmpCurveWidth=Math.abs(automata.getStateGeometricData(vertexMapList.get(c)).getX()-
                                        automata.getStateGeometricData(phaseList.get(d)).getX());
                                System.out.println("----tmpheight= "+tmpHeight);
                        
                            }
                            
                            
                        }
                        System.out.println("    higher "+tmpSourceInd+" -> "+tmpTargetInd);
                        System.out.println("    height= "+height);
                        System.out.println("    a= "+tmpCurveWidth);

                    }
                    //if(tmpSourceInd>targetInd) break;
                }
//                controlPoint1.setY(height-heightScale*(1+higher/8));                    
//                controlPoint2.setY(height-heightScale*(1+higher/8));                    
//                controlPoint1.setLocation(controlPoint1.getX(),height-heightScale*(1+higher/8));                    
//                controlPoint2.setLocation(controlPoint2.getX(),height-heightScale*(1+higher/8));                    
                //controlPoint1.setLocation(controlPoint1.getX(),height-heightScale);                    
                //controlPoint2.setLocation(controlPoint2.getX(),height-heightScale); 
                
                double h_=height-lineY;
                double a_=tmpCurveWidth;
//                if(a_==0) a_=Math.min(Math.abs(automata.getStateGeometricData(vertexMapList.get(middleInd)).getX()-
//                                        automata.getStateGeometricData(edge.getSourceState()).getX()),
//                                       Math.abs(automata.getStateGeometricData(vertexMapList.get(middleInd)).getX()-
//                                        automata.getStateGeometricData(edge.getTargetState()).getX()));
                
                double A_=Math.abs(automata.getStateGeometricData(edge.getSourceState()).getX()-
                                        automata.getStateGeometricData(edge.getTargetState()).getX());
                System.out.println("A= "+A_+" a="+a_+" h= "+h_+" midInd= "+middleInd);
                
                double hh;
                if(a_==0) hh=height+heightScale;                    
                else hh=lineY+h_*Math.pow(A_/a_, 1.1);                   
                
                
                //if(hh>lineY+bound.height*4)hh=lineY+bound.height*4;
                
                controlPoint1.setLocation(controlPoint1.getX(),hh);                    
                
                
                //else 
//                controlPoint1.setLocation(controlPoint1.getX(),lineY+h_*Math.pow(A_/a_, 1.5));                    
                
                
                System.out.println("higher "+higher+" "+controlPoint1.getY());
                
            }else{
                int index=addedBottomEdge.indexOf(edge);
                int len=addedBottomEdge.size();

                //double height=lineY;
                //int middleInd=(sourceInd+targetInd)/2;
                //double middleHeight=vertexMapList.get(middleInd).getGeometry().getHeight();
                
                // find the one has largest height between source and target
                int middleInd=(int)((float)(a+b)/2);
                double middleHeight=lineY;
                for(int i=b+1;i<a;++i){
//                    double tmpHeight=vertexMapList.get(i).getGeometricData().getY();
                    double tmpHeight=phaseList.get(i).getGeometricData().getY();
                    if(middleHeight>tmpHeight){ 
                        middleInd=i;
                        middleHeight=tmpHeight;
                    }
                }
//                middleHeight=automata.getStateGeometricData(vertexMapList.get(middleInd)).getHeight();
                middleHeight=automata.getStateGeometricData(phaseList.get(middleInd)).getHeight();
                
                double heightScale=middleHeight/4;
                
//                double height=vertexMapList.get(middleInd).getGeometricData().getY()-middleHeight/2;
                double height=phaseList.get(middleInd).getGeometricData().getY()-middleHeight/2;
                
                double higher=0;
                
                double tmpCurveWidth=0;
                
                
                for(int i=0;i<len;++i){
                    
                    Transition tmp=addedBottomEdge.get(i);
//                    int tmpSourceInd=vertexMapList.indexOf(tmp.getSourceState());
                    int tmpTargetInd=vertexMapList.indexOf(tmp.getTargetState());
                    int tmpSourceInd=phaseList.indexOf(tmp.getSourceState());
                    
                   // System.out.println("bottom child "+tmpSourceInd+" -> "+tmpTargetInd);

                    int c=tmpSourceInd;
                    int d=tmpTargetInd;
                    if(tmpTargetInd>tmpSourceInd){ c=tmpTargetInd; d=tmpSourceInd;}
                    //System.out.println("["+a+" , "+b+"]"+"--["+c+" , "+d+"]");


                    if(a>=c && b<=d && !(a==c && b==d)){
//                      if(a>=c && b<=d){
                        if(tmp.getGeometricData().controlPoints.size()>0){
                            double tmpHeight=tmp.getGeometricData().controlPoints.get(0).getY();
                            if(height>tmpHeight){
                                height=tmpHeight; higher++;
//                                tmpCurveWidth=Math.abs(automata.getStateGeometricData(vertexMapList.get(c)).getX()-
//                                        automata.getStateGeometricData(vertexMapList.get(d)).getX());
                                tmpCurveWidth=Math.abs(automata.getStateGeometricData(phaseList.get(c)).getX()-
                                        automata.getStateGeometricData(phaseList.get(d)).getX());
                                
                                System.out.println("----tmpheight= "+tmpHeight);
                        
                            }else if(height==tmpHeight){
                                height*=1.15; higher++;
                                tmpCurveWidth=Math.abs(automata.getStateGeometricData(phaseList.get(c)).getX()-
                                        automata.getStateGeometricData(phaseList.get(d)).getX());
                                
                                System.out.println("----tmpheight= "+tmpHeight);
                        
                            }
                        }
                        System.out.println("    higher "+tmpSourceInd+" -> "+tmpTargetInd);
                        System.out.println("    height"+height);
                        System.out.println("    a= "+tmpCurveWidth);

                    }
                    
                 }
//                 controlPoint1.setY(height+heightScale*(1+higher/8));                    
//                 controlPoint2.setY(height+heightScale*(1+higher/8));                    
//                 controlPoint1.setLocation(controlPoint1.getX(),height+heightScale*(1+higher/8));                    
//                 controlPoint2.setLocation(controlPoint2.getX(),height+heightScale*(1+higher/8));                    
//                controlPoint1.setLocation(controlPoint1.getX(),height+heightScale);                    
                //controlPoint2.setLocation(controlPoint2.getX(),height+heightScale);                    
                double h_=height-lineY;
                double a_=tmpCurveWidth;
//                if(a_==0) a_=Math.min(Math.abs(automata.getStateGeometricData(vertexMapList.get(middleInd)).getX()-
//                                        automata.getStateGeometricData(edge.getSourceState()).getX()),
//                                       Math.abs(automata.getStateGeometricData(vertexMapList.get(middleInd)).getX()-
//                                        automata.getStateGeometricData(edge.getTargetState()).getX()));
                double A_=Math.abs(automata.getStateGeometricData(edge.getSourceState()).getX()-
                                        automata.getStateGeometricData(edge.getTargetState()).getX());
                System.out.println("A= "+A_+" a="+a_+" h= "+h_+" middleInd= "+middleInd);
                
                double hh;
                if(a_==0) hh=height-heightScale;                    
                else hh=h_*Math.pow(A_/a_,1.1);                    
                
                //if(hh<lineY-bound.height*4) hh=lineY-bound.height*4;
                controlPoint1.setLocation(controlPoint1.getX(), hh);
                
            }
              
        
       }
         
     }        
            points.clear();
            if(controlPoint1!=null){
                points.add(controlPoint1);
                if(controlPoint1.getY()>upboundY) upboundY=controlPoint1.getY();
                
                if(controlPoint2!=null){
                    points.add(controlPoint2);
                    if(controlPoint2.getY()>upboundY) upboundY=controlPoint2.getY();
                }
//                System.out.println(controlPoint1);

                //edge.getGeometry().setPoints(points);
                TransitionGeometricData tgd=automata.getTransitionGeometricData(edge);
                tgd.controlPoints=points;
                automata.setTransitionGeometricData(edge, tgd);
            }
//             placeLableToBound(edge);
            
            
            
    }
    /*
     *  add a third control point at the top of the curve
     */
    private void placeLableToBound(Transition edge){
        //graph.refresh();
        
        Rectangle curvebound=automata.getTransitionBound(edge);
        
//        mxCellState state=(graph.getView().getCellStates(new Object[]{edge}))[0];
//        List<mxPoint> points=state.getAbsolutePoints();
        if(edge.getGeometricData().controlPoints.size()==1){
            automata.resetTransitionControlPoint(edge);
        }
        if(edge.getGeometricData().controlPoints.size()==2){
        
//            mxCurve curve=new mxCurve(points);
//            //System.out.println("points size="+points.toString()+"--"+points.size());
//            mxRectangle rec2=curve.getBounds();
//        

            Point2D pt1=edge.getGeometricData().controlPoints.get(0);
            double centerx=curvebound.getCenterX();//pt1.getX()+pt2.getX())/2;
            double centery=(pt1.getY()>lineY)?curvebound.getY():curvebound.getY()-curvebound.getHeight();//+(double)rec.getHeight()*((pt1.getY()<lineY)? -1 :1);
            
            //edge.getGeometry().getPoints().add(1,new mxPoint(centerx,centery));
            automata.addTransitionControlPoint(edge, new Point2D.Double(centerx,centery),1);
            System.out.println("add cpt: "+centerx+" , "+centery);
//            if(pt1.getY()>lineY) edge.getGeometricData().setY(edge.getGeometry().getY()*-1);
            
            //else edge.getGeometry().setY(edge.getGeometry().getY());
            
            // get the heighest y
            if(upboundY>centery) upboundY=centery;
        }
        
        
    }
    /*
     * Temporary Use:
     * move the whole graph if there are control points outside canvas
     * (with y<0)
     */
    private void moveWholeBound(){
        //graph.refresh();
        
        double offsetx=upboundY;//-bound.getMaxX()+50;
        
        automata.moveStates(vertexMapList, new Point2D.Double(0,-offsetx));
        System.out.println("move all vertices: "+offsetx);
        
        
//        upboundY*=-1;
//        upboundY+=30;
                
//        List<State> allvertices=this.getSelectedVertices(automata);
//        for(State vertex:allvertices){
////            double newh=vertex.getGeometricData().getY()+upboundY;
////            //vertex.getGeometry().setY(newh);
////            StateGeometricData sgd=vertex.getGeometricData();
////            sgd.setY(newh);
////            automata.setStateGeometricData(vertex, sgd);
//            automata.moveState(vertex, new Point2D.Double(0,-upboundY));
//        }
        
        //Object[] alledges=graph.getChildEdges(graph.getDefaultParent());
//        List<Transition> alledges=automata.getAllTransitions();
//        
//        for(Transition edge:alledges){
//            //mxCell edge=(mxCell)oedge;
//            List<Point2D> cpts=edge.getGeometricData().controlPoints;
//            for(Point2D pt:cpts){
//                double newh=pt.getY()+boundY;
//                pt.setLocation(pt.getX(),newh);
//            }
//            TransitionGeometricData tgd=automata.getTransitionGeometricData(edge);
//            tgd.controlPoints=cpts;
//            automata.setTransitionGeometricData(edge, tgd);
//            //edge.getGeometricData().setPoints(cpts);
//        }
    } 
    
    public void setUseVertexGroup(boolean use){
        useVertexGroup=use;
    }
    
    
}