package com.juliar.controlflow;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Don on 2/1/2017.
 */
public class ControlFlowAdjacencyList {
    private List<ControlFlowNode> list = new ArrayList<ControlFlowNode>();
    private LinkedBlockingDeque<ControlFlowNode> queue = new LinkedBlockingDeque<>();

    public void addNode(String caller, String callee){
        ControlFlowNode parent = findNode(caller);
        ControlFlowNode child = findNode(callee);
        addNext(parent, child);
    }

    public void walkGraph(){
        ControlFlowNode main = findNode("main");
        walkGraph( main );
    }

    private void walkGraph(ControlFlowNode main){
        try {
            //System.out.println(main.functionName);
            main.visited = true;
            queue.addFirst(main.next);
            while (!queue.isEmpty()) {
                ControlFlowNode g = queue.removeLast();
                if (!g.visited) {
                    //System.out.println(g.functionName);
                    g.visited = true;
                }
                while (g != null) {
                    if (!g.visited) {
                        queue.addFirst(g);
                    }
                    g = g.next;
                }
            }
        }
        catch(Exception e){

        }

    }

    private void addNext(ControlFlowNode p, ControlFlowNode n){
        if (p.next == null){
            p.next = n;
            return;
        }
        else if(p.next != n){
            addNext(p.next, n);
        }
    }

    private ControlFlowNode findNode(String name){
        long count = list.stream().filter( f -> f.functionName.equals(name)).count();
        if (count == 0) {
            ControlFlowNode n = new ControlFlowNode(name);
            list.add(n);
            return n;
        }

        for(ControlFlowNode node : list){
            if (node.functionName.equals(name)){
                return node;
            }
        }

        throw new RuntimeException("cfa failed");
    }
}

class ControlFlowNode{
    public ControlFlowNode next;
    public String functionName;
    public boolean visited = false;

    public ControlFlowNode(String funcName){
        functionName = funcName;
        next = null;
    }
}