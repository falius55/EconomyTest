import java.util.*;
import java.awt.*;
import java.awt.event.*;

class Graph extends Canvas implements MouseListener{
	java.util.List<Map<String,Object>> dataList;
	int move = 0;
	GregorianCalendar cal;
	String kind ="";
	int startDays;
	TextArea logArea;
	int dx = 0;

	public Graph(java.util.List<Map<String,Object>> dataList,TextArea logArea){
		this.dataList = Collections.synchronizedList(new ArrayList<Map<String,Object>>());
		for(int i=0;i<dataList.size();i++){
			this.dataList.add(dataList.get(i));
			}
			this.logArea = logArea;

			addMouseListener(this);
		}

	public void addData(Map<String,Object> map){
		dataList.add(map);
		}
	public void newData(Map<String,Object> map){
			if(dataList.size()==0) return;
			dataList.remove(dataList.size()-1);
			dataList.add(map);
		}

	public void setPrev(int prev){
		move -= prev;
		repaint();
		}
	public void setNext(int next){
		move += next;
		repaint();
		}

	public void setKind(String kind){
		this.kind = kind;
		repaint();
		}

		public void paint(Graphics g){
			Dimension d = getSize();

			g.setColor(Color.blue);

			int[] xPoints = new int[d.width];
			int[] yPoints = new int[d.width];

			long maxData = Long.MIN_VALUE;
			long minData = Long.MAX_VALUE;
			for(int i=0;i<dataList.size();i++){
				maxData = Math.max(maxData,((Long)dataList.get(i).get(kind)).longValue());
				minData = Math.min(minData,((Long)dataList.get(i).get(kind)).longValue());
				}

				long base = (maxData-minData)/(d.height*9/10); // data/y
				if(base==0) base = 1;
			Point[] point = new Point[d.width];
			int x = 0;
			int k = 0;
			if(dataList.size()>d.width || dataList.size()==0){
				dx = 1;
				}else{
					dx = d.width/dataList.size();
					}	
			int size = dataList.size();
			int width = d.width;
			
			if(size<=width){
				move = 0;
				}else if(size-width+move<0){
					move = -(size-width);
				}else if(move > 0){
					move = 0;
					}else{
						}
				g.setColor(Color.BLUE);
				startDays = size>width ? size-width+move:0;
			for(int i=startDays;i<size+move;i++){
				long data = (long)dataList.get(i).get(kind) - minData;
				
				int y = d.height-(int)(data/base) - (d.height/10);
				point[k++] = new Point(x,y);
				x += dx;
				if(i%100 == 0) g.drawString("days:"+i+"\nvalue:"+(data+minData),x,y);
				}

			// yline
			long num = maxData - minData;
			int count = 0;
			while(num!=0){
				num /= 10;
				count++;
				}
				
				g.setColor(Color.GREEN);
				long yLineBase;
				if(count>2){
				yLineBase = ((maxData - minData) - (maxData-minData)%(long)(Math.pow(10,(double)count-2)))/10;
				}else{
					yLineBase = (maxData-minData)/10;
					}
			for(int i=0;i<10;i++){
				int y = d.height-(int)(yLineBase*i/base) - (d.height/10);
				System.out.println("yLine:"+y);
				g.drawLine(0,y,width,y);
				g.drawString("value:"+((int)(yLineBase*i)+minData),0,y);
				}

			System.out.println("dataList:"+dataList.size());
			System.out.println("d.width:"+d.width);
			System.out.println("d.height:"+d.height);


				g.setColor(Color.RED);
				for(int i=0;i<k-1;i++){
					g.drawLine((int)point[i].getX(),(int)point[i].getY(),(int)point[i+1].getX(),(int)point[i+1].getY());
				//	g.drawString("("+(int)point[i].getX()+","+(int)point[i].getY()+")",(int)point[i].getX(),(int)point[i].getY());
					}
			}

		// MouseListener
		public void mouseClicked(MouseEvent e){
			if(dataList.size()==0) return;
			int i = startDays+e.getX()/dx;
			if(i>dataList.size()-1) i = dataList.size()-1;
			long data = (long)dataList.get(i).get(kind);
			logArea.append((i+1)+"日目の"+kind+"の値："+data+"\n");
			System.out.println("startDays:"+startDays+"\ndx:"+dx);
			System.out.println("clickX:"+e.getX()+",clickY:"+e.getY());
			}

		public void mouseEntered(MouseEvent e){}
		public void mouseExited(MouseEvent e){}
		public void mousePressed(MouseEvent e){}
		public void mouseReleased(MouseEvent e){}
	}
