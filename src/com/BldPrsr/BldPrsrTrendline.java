package com.BldPrsr;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.model.XYSeries;

public class BldPrsrTrendline 
{

	private double	firstX;
	private double lastX;
    private int count;
    private double xAxisValuesSum;
    private double xxSum;
    private double xySum;
    private double yAxisValuesSum;
    private List<Double> values;
    private XYSeries trendVals;
	public static String TAG="BldPrsr";
	public static String SubTag="BldPrsrTrendline: ";

    public BldPrsrTrendline(String lbl)
    {
        this.count = 0;
        this.xxSum = 0;
        this.xySum = 0;
        this.xAxisValuesSum = 0;
        this.yAxisValuesSum = 0;
        // this.values = new XYSeries("");
        this.trendVals = new XYSeries(lbl);
        values = new ArrayList<Double>();
        BldPrsrLogger.i(TAG, SubTag + "Internal data is initialized");
    }

    public double Slope;
    public double Intercept;
    public double Start;
    public double End;

/*
    private void Initialize()
    {
        this.count = this.yAxisValues.Count;
        this.yAxisValuesSum = this.yAxisValues.Sum();
        this.xAxisValuesSum = this.xAxisValues.Sum();
        this.xxSum = 0;
        this.xySum = 0;

        for (int i = 0; i < this.count; i++)
        {
            this.xySum += (this.xAxisValues[i]*this.yAxisValues[i]);
            this.xxSum += (this.xAxisValues[i]*this.xAxisValues[i]);
        }

        this.Slope = this.CalculateSlope();
        this.Intercept = this.CalculateIntercept();
        this.Start = this.CalculateStart();
        this.End = this.CalculateEnd();
    }
*/
    public void addXY(int xI, int yI)
    {
    	double x,y;
    	
    	x = (double)xI;
    	y = (double)yI;
    	
    	if ( this.count == 0 )
    		firstX = x;
    	lastX = x;
    	this.count++;
    	this.xAxisValuesSum += x;
    	this.yAxisValuesSum += y;
    	this.xxSum += (x*x);
    	this.xySum += (x*y);
    	this.values.add(y);
    	// BldPrsrLogger.i(TAG, SubTag + "Adding (" + x + "," + y + ")");
    }
    
    private double CalculateSlope()
    {
        try
        {
        	double sl;
            sl = ((this.count*this.xySum) - (this.xAxisValuesSum*this.yAxisValuesSum))/
            		((this.count*this.xxSum) - (this.xAxisValuesSum*this.xAxisValuesSum));
            BldPrsrLogger.i(TAG, SubTag + "Slope:" + sl );
            return sl;
        }
        catch (Exception e)
		{
			BldPrsrLogger.e(TAG, SubTag + e.getMessage());
            return 0;
        }
    }

    private double CalculateIntercept()
    {
    	double	inter = (this.yAxisValuesSum - (this.Slope*this.xAxisValuesSum))/this.count;
    	BldPrsrLogger.i(TAG, SubTag + "Intercept:" + inter );
        return inter;
    }

    private double CalculateStart()
    {
    	double	st = (this.Slope*this.firstX) + this.Intercept;
    	BldPrsrLogger.i(TAG, SubTag + "Start:" + st );
        return st;
    }

    private double CalculateEnd()
    {
    	double	end = (this.Slope*this.lastX) + this.Intercept;
        BldPrsrLogger.i(TAG, SubTag + "End:" + end );
        return end;
    }
 
    public XYSeries getTheTrend()
    {
    	int	i;
    	double	delta;
    	double	val;

    	if (trendVals.getItemCount() > 0) {
    		// Clean up the trend.
    		trendVals.clear();
    	}
    	this.Slope = CalculateSlope();
    	this.Intercept = CalculateIntercept();
    	this.Start = CalculateStart();
    	this.End = CalculateEnd();
    	
    	val = this.Start;
   	
    	delta = (this.End - this.Start)/(double)this.count;
    	
    	trendVals.add(0, this.Start);
    	// trendVals.add(1, val);
    	for ( i = 1; i < this.count; i++ )
    	{
    		val += delta;
    		// trendVals.add(i, val);
    	}
    	// trendVals.add(i, val);
    	trendVals.add(i, this.End);
    	return trendVals;
    }
}