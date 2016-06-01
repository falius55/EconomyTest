import java.io.*;
import java.util.*;

class CentralBank{
	private static int amountCurrency = 0; // 貨幣発行量
	static int keepBond = 0; // 保有国債残高
	static double capitalRate = 0.16; // 自己資本比率
	static Gorvement nation;
	static ArrayList<PrivateBank> bankList; // 民間銀行のリスト
	
	static{
		bankList = new ArrayList<PrivateBank>();
		}
	// 国債買い取り(買いオペ)
	public static int buyBond(int amount){
		int yetBuyed = amount;
		// 各銀行に買い取り国債割り当て
		for(int i=0;i<bankList.size() || yetBuyed!=0;i++){
			if(bankList.get(i).keepBond >0 && bankList.get(i).keepBond < yetBuyed){
				yetBuyed -= bankList.get(i).keepBond;
				bankList.get(i).keepBond = 0;
				}else if(bankList.get(i).keepBond >0){
					bankList.get(i).keepBond -= yetBuyed;
					yetBuyed = 0;
					}else{
						}
			}
		// 売れ残りがあるか
		int newKeep = 0;
		if(yetBuyed==0){
			newKeep = amount;
			}else{
				newKeep = amount - yetBuyed;
				}
		keepBond += newKeep;
		return createCurrency(newKeep);
		}

	// 通貨発行
	public static int createCurrency(int amount){
		amountCurrency += amount;
		return amount;
		}

	// ゲッター
	public static int getAmountCurrency(){
		return amountCurrency;
		}
	public static double getCapitalRate(){
		return capitalRate;
		}

	public static void setInstance(Gorvement getNation){
		nation = getNation;
		}
	public static void setInstance(PrivateBank bank){
		bankList.add(bank);
		}
	}
