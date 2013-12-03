
package edu.mbl.jif.ps.orient;

/*
 *
 * @author GBH
*/
public class CircularHistogram {

  int [] bins = null;
  int nBins;
  float xLow,xHigh;
  float delBin;
   int overFlows=0,underFlows=0;
  //DataChart  chart;
  //String dataString=null;


  //----------------------------------------------------------------
  CircularHistogram (int nBins, float xLow, float xHigh,int _width,int _height){
  
   this.nBins = nBins;
   this.xLow  = xLow;
   this.xHigh = xHigh;

   bins = new int[nBins];
   delBin = (xHigh-xLow)/(float)nBins;

   //reset();
   //chart = new DataChart(dataString, _width, _height);

  }

  //----------------------------------------------------------------
  // Extra constructor to allow for double values
  CircularHistogram (int nBins, double xLow, double xHigh,int _width,int _height){
    this(nBins, (float) xLow, (float) xHigh, _width, _height);
  }

  //----------------------------------------------------------------
  void setData(double data){
    setData((float)data);
  }
  //----------------------------------------------------------------
  void setData(float data){
   
   if( data < xLow)
     underFlows++;
   else if ( data >= xHigh) 
     overFlows++;
   else{
     int bin = (int)((data-xLow)/delBin);
     if(bin >=0 && bin < nBins) bins[bin]++;
   }
  }    

  //----------------------------------------------------------------
  // To display the histogram in a chart, we need to pass the data
  // as a string. 
//  public void graphIt(){
//    dataString = "";
//    for (int i=0; i<nBins; i++){
//      dataString += bins[i] + " ";
//    }
//    //chart.setData(dataString);
//    //chart.graphIt();
//  }
  

  //----------------------------------------------------------------
//  public void reset(){
//    dataString = "";
//    for (int i=0; i<nBins; i++){
//      bins[i]=0;
//      dataString = dataString + bins[i] + " ";
//    }
//  }
 	
}