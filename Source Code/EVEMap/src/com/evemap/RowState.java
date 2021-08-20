package com.evemap;

import com.evemap.objects.Alliance;


public class RowState{

	 private int qMax, currthread;
     private Alliance[] prevRow = new Alliance[0];
     private double[] prevInf = new double[0];
     private boolean[] curBorder = new boolean[0];
     private Alliance[] curRow = new Alliance[0];
     
     public RowState(){}

	public synchronized int getQMax() {
		return qMax;
	}

	public synchronized void setQMax(int max) {
		qMax = max;
	}

	public synchronized Alliance[] getPrevRow() {
		return prevRow;
	}

	public synchronized void setPrevRow(Alliance[] prevRow) {
		this.prevRow = prevRow;
	}

	public synchronized double[] getPrevInf() {
		return prevInf;
	}

	public synchronized void setPrevInf(double[] prevInf) {
		this.prevInf = prevInf;
	}

	public synchronized boolean[] getCurBorder() {
		return curBorder;
	}

	public synchronized void setCurBorder(boolean[] curBorder) {
		this.curBorder = curBorder;
	}

	public synchronized Alliance[] getCurRow() {
		return curRow;
	}

	public synchronized void setCurRow(Alliance[] curRow) {
		this.curRow = curRow;
	}

	public int getCurrthread() {
		return currthread;
	}

	public void setCurrthread(int currthread) {
		this.currthread = currthread;
	}
}
