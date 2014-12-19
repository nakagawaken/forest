import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class ForectServer {

	// 正常データその１
	static byte []rssiXY1 = {
		                    // X=0.9m   // Y=3.2m
		0x02,0x10,0x06,0x00,0x09, 0x00, 0x20, 0x00, 0x00, 0x00, 0x03
	};

	// 正常データその２
	static byte []rssiXY2 = {
        	           // X=1.0m   // Y=6.4　マイナスm
		0x02,0x10,0x06,0x00,0x0a, 0x00, 0x40, 0x00, 0x00, 0x00, 0x03

	};

	// 異常　データフォーマット
	static byte []rssiErrorFormat = {
		                      // ↓ここがエラーコード
		0x02,0x10, 0x01, 0x00, 0x04, 0x03
	};

	// 異常　RSSIデータ異常
	static byte []rssiErrorRSSI = {
                             // ↓ここがエラーコード
		0x02,0x10, 0x01, 0x00, 0x03, 0x03
	};

	// 　重複データ
	static byte []rssiDupicate = {
        	                 // ↓ここがエラーコード
		0x02,0x10, 0x01, 0x00, 0x02, 0x03

	};

	// データ不足
	static byte []rssiUnknown = {
                             // ↓ここがエラーコード
		0x02,0x10, 0x01, 0x00, 0x01, 0x03
	};

	// 第一引数　ポート番号
	// ↑eclipseなら起動設定　コマンドプロンプトなら引数
	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
	      // ソケットや入出力用のストリームの宣言

        ServerSocket echoServer = null;
        String line;
        BufferedReader is;
 //       PrintStream os;
        DataOutputStream os;
        Socket clientSocket = null;


		// 受信データ1バイト分
		int iReceived = 0;

		// 返却用のデータ　カウンター
		int iRssi = 0;

     // 引数をチェックする。
     		if (args.length != 1) {
     			System.err.println("usage: java ForestServer  port");
     			return;
     		}

        // ポートを開く
        try {
            echoServer = new ServerSocket(Integer.parseInt(args[0]));
        }
        catch (IOException e) {
            System.out.println(e);
        }

        System.out.println("ServerSocket start");

        // クライアントからの要求を受けるソケットを開く
        try {
            clientSocket = echoServer.accept();
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         //   os = new PrintStream(clientSocket.getOutputStream());
               os = new DataOutputStream(clientSocket.getOutputStream());

            int starttx = 0; // 0開始前 1:開始後
            int endtx = 0; // 0終了前 1:終了後
            int txcount = 0; // 何バイト読んだか

            // クライアントからのメッセージを待つ
    		ByteArrayOutputStream ba = new ByteArrayOutputStream();


            while (true) {
            	//line = is.readLine();

                //受け取ったメッセージをそのまま返す
                //●返却用の加工
                // まずはバイナリ(52バイト分）→テキスト変換して出力

            	iReceived = is.read();


            	//if ((char)iReceived !='\r'){　←これだと\rを送ってもらう必要あり
            	if ((char)iReceived == 0x02 && starttx == 0 ){ // 開始
            		starttx = 1; // フラグを立てておく
            		txcount++;
            		ba.write(iReceived);

                    System.out.println("Server STX");

            	} else if ((char)iReceived == 0x03 && txcount == 50) { // 終了
            		ba.write(iReceived);

                    System.out.println("Server ETX count="+txcount);

            		byte[] bufferbyte = ba.toByteArray();

		    		StringBuffer buffertx = new StringBuffer();
		    		for (int i = 0 ; i < bufferbyte.length; i++){
		    			buffertx.append( String.format("%02X", bufferbyte[i])  );
		    		}
		    		buffertx.append('\n'); // 改行追加
		    		System.out.println(buffertx.toString());

		    		// 初期化処理
		    		ba.flush(); // streamにデータが残らないように
		    		ba = new ByteArrayOutputStream();
            		starttx = 0; // フラグを戻す
            		txcount = 0; // カウンタを戻す

	        //        os.println("Server Message"); // クライアントへ返信


            		switch(iRssi) {
             		case 0:
             			os.write(rssiXY1);
            		break;
             		case 1:
             			os.write(rssiXY2);
            		break;
             		case 2:
             			os.write(rssiErrorFormat);
            		break;
             		case 3:
             			os.write(rssiErrorRSSI);
            		break;
             		case 4:
             			os.write(rssiDupicate);
            		break;
             		case 5:
             			os.write(rssiUnknown);
            		break;
            		default:
             			os.write(rssiXY1);
            		break;
            		}
            		iRssi++;

            	} else if ((char)iReceived == 0x03 && txcount == 46) { // 終了
            		ba.write(iReceived);

                    System.out.println("Server ETX count="+txcount);

            		byte[] bufferbyte = ba.toByteArray();

		    		StringBuffer buffertx = new StringBuffer();
		    		for (int i = 0 ; i < bufferbyte.length; i++){
		    			buffertx.append( String.format("%02X", bufferbyte[i])  );
		    		}
		    		buffertx.append('\n'); // 改行追加
		    		System.out.println(buffertx.toString());

		    		// 初期化処理
		    		ba.flush(); // streamにデータが残らないように
		    		ba = new ByteArrayOutputStream();
            		starttx = 0; // フラグを戻す
            		txcount = 0; // カウンタを戻す

	        //        os.println("Server Message"); // クライアントへ返信


            		switch(iRssi) {
             		case 0:
             			os.write(rssiXY1);
            		break;
             		case 1:
             			os.write(rssiXY2);
            		break;
             		case 2:
             			os.write(rssiErrorFormat);
            		break;
             		case 3:
             			os.write(rssiErrorRSSI);
            		break;
             		case 4:
             			os.write(rssiDupicate);
            		break;
             		case 5:
             			os.write(rssiUnknown);
            		break;
            		default:
             			os.write(rssiXY1);
            		break;
            		}
            		iRssi++;

            	} else if (txcount > 100) {
            		// 無限ループ対策

    	    		System.out.println("txcount="+txcount);
    	    		break;  // 終了
            	} else { // 開始と終了以外の場合
            		txcount++;
            		ba.write(iReceived);

                    System.out.println("Server count="+ txcount+
                    		" Data=" +String.format("%02X", iReceived) );
            	}


            }
            ba.close();

        }
        catch (IOException e) {
            System.out.println(e);
        }
    }



}
