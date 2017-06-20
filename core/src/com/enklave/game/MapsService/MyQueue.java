package com.enklave.game.MapsService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class MyQueue implements Queue{
    private static MyQueue ourInstance = new MyQueue();

    public static MyQueue getInstance() {
        return ourInstance;
    }
    ArrayList<Double> queuelat;
    ArrayList<Double> queuelong;
    int index;

    private MyQueue() {
        this.queuelat =new ArrayList<Double>();
        this.queuelong =new ArrayList<Double>();
        index = 0;
    }

    @Override
    public int size() {
        return index;
    }

    @Override
    public boolean isEmpty() {
        return (this.queuelat.isEmpty() && this.queuelong.isEmpty());
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }
    public boolean contains(Double l,Double ln) {
        if(this.queuelat.contains(l) && this.queuelong.contains(ln))
            return true;
        else
            return false;
    }

    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return this.queuelat.toArray();
    }

    @Override
    public List[] toArray(Object[] objects) {
        return new List[0];
    }

    @Override
    public boolean add(Object o) {
        return  false;
    }
    public boolean add(Double lt,Double ln){
        if(this.queuelat.add(lt) && this.queuelong.add(ln)){
            index++;
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean addAll(Collection collection) {
        index += collection.size();
        return this.addAll(collection);
    }

    @Override
    public void clear() {
        index = 0;
        this.queuelat.clear();
        this.queuelong.clear();
    }

    @Override
    public boolean retainAll(Collection collection) {
        return this.queuelat.retainAll(collection);
    }

    @Override
    public boolean removeAll(Collection collection) {
        index =0;
        return this.removeAll(collection);
    }

    @Override
    public boolean containsAll(Collection collection) {
        return this.containsAll(collection);
    }

    @Override
    public boolean offer(Object o) {
        return false;
    }

    @Override
    public Double[] remove() {
        Double[] aux=new Double[2];
        aux[0]=this.queuelat.remove(0);
        aux[1]=this.queuelong.remove(0);
        index--;
        return aux;
    }

    @Override
    public Object poll() {
        return null;
    }

    @Override
    public Object element() {
        return null;
    }

    @Override
    public Object peek() {
        return null;
    }
}
