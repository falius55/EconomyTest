import java.io.*;
import java.util.*;

// 税額を返すクラス
public class Tax{
	// 消費税額
	static double CONSUMPTION_TAX = 0.08;
	// 所得税(月額)
	// 超過累進税率
	static double[][] INCOME_TAX = { // {下限,税率}
		{0,0.05},			// 0円以上、1,950,000円以下は5%
		{1950000,0.1},		// 1,950,000円以上、3,300,000円以下は10%
		{3300000,0.2},
		{6950000,0.23},
		{9000000,0.33},
		{18000000,0.4},
		{40000000,0.45}
	};
	static double[][] CORPORATION_TAX = {
		{0,0.15},
		{8000000,0.255}
		};

	// 消費税関数
	// 税込み額　→　消費税額
	public static int getConsumptionTax(int cons){
		return (int)(cons - ((double)cons / (1 + CONSUMPTION_TAX)));
		
		}
	// 税抜き価格　→　税込価格
	public static int getInConsumptionTax(int cons){
		return (int)(cons * (1 + CONSUMPTION_TAX));
		}
	
	// 所得税関数
	// 年収額　→　1年分の所得税額
	public static int getIncomeTax(int income){
		return (int)(getProgressiveTax(income,INCOME_TAX) * 1.021);
		}
	// 法人税
	public static int getCorporationTax(int income){
		if(income < 0) return 0;
		return getProgressiveTax(income,CORPORATION_TAX);
		}
	// 超過累進税額の取得
	public static int getProgressiveTax(int income,double[][] taxBox){
		double tax = 0;
			// 所得税額の計算
			// 所得税率の分類の数だけ繰り返す
		for(int i = 0;i<taxBox.length;i++){
			// 最高分類以上の収入があれば、収入から最高分類の金額を控除した残額に最高分類の税率をかけて税額化する。i+1を判定に使う条件式に入る前にループを抜ける。
			if(i==taxBox.length-1){
				tax += (income-taxBox[i][0])*taxBox[i][1];
				break;
				}
				if(income > taxBox[i+1][0]){
				// 収入が現分類より一つ上の分類超あるなら、現分類の一つ上の分類額から現分類の金額を控除した残額に現分類の税率をかけて税額化する
				tax += (taxBox[i+1][0]-taxBox[i][0])
										*taxBox[i][1];
				}else{
					// 収入が現分類より一つ上の分類以下ならば、収入から現分類の金額を控除した残額に現分類の税率をかけて税額化する
					tax += (income-taxBox[i][0])*taxBox[i][1];
					break;
					}
			}
		return (int)tax;
		}

	}
	
