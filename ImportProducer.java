import java.io.*;
import java.util.*;

public class ImportProducer extends Producer{
	
	static Map<String,Integer> productMap;
	// コンストラクタ
	public ImportProducer(){
		setIndustries("輸入会社");
			productMap = new HashMap<String,Integer>();
			materialMap = new HashMap<String,String>();
		}

	// ****セッター****//
	// 業種設定 + 商品リスト作成
	// 価格は税込み
	@Override
	public void setIndustries(String industries){
		this.industries = industries;
				productMap.put("石炭",120);
		}
	}
