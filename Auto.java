import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.*;
import java.awt.event.*;

class Auto extends Thread {
	private boolean running = true;
	EconomyTest et;
	Human[] humans;
	Producer[] producers;
	Gorvement nation;
	PrivateBank bank;

	public Auto(){
		super();
		}

	public Auto(String str){
		super(str);
		}

		public void run(){
			while(running){
				EconomyTest.actionAuto(500);
				}
			}

	public void stopRunning(){
		running = false;
		}
	}
