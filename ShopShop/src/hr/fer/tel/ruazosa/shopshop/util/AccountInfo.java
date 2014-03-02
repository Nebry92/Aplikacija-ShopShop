package hr.fer.tel.ruazosa.shopshop.util;

import hr.fer.tel.ruazosa.shopshop.DropboxConnect;

import com.dropbox.client2.exception.DropboxException;


/**
 * Thread used to get user information from Dropbox Server
 *
 */
public class AccountInfo extends Thread{
	  private String accInfo = "";
	 
	  public String getAccInfo(){
	    return accInfo;
	  }
	 
	  @Override
	  public void run(){
		  try {
				accInfo = DropboxConnect.mApi.accountInfo().displayName;
			} catch (DropboxException e) {
				e.printStackTrace();
			}
	  }
	 
}
