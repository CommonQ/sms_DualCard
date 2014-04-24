/*
 * Copyright (C) 2008 Esmertec AG.
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.bupt.mms.model;

import edu.bupt.mms.ContentRestrictionException;
import edu.bupt.mms.dom.events.EventImpl;
import edu.bupt.mms.dom.smil.SmilMediaElementImpl;
import edu.bupt.mms.ui.MessageUtils;

import com.google.android.mms.MmsException;
import android.database.sqlite.SqliteWrapper;

import org.w3c.dom.events.Event;
import edu.bupt.mms.dom.smil.*;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.provider.Telephony.Mms.Part;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


	/**
	 * ??ANT???
	 * qq
	 * vcardmodel?,???MediaModel?
	 * 
	 * ????58,????:????
	 * 	 * 
	 * */
public class VCardModel extends MediaModel {
    private static final String TAG = MediaModel.TAG;
    private static final boolean DEBUG = true;
    private static final boolean LOCAL_LOGV = true;

    private final HashMap<String, String> mExtras;


	/**
	 * ??ANT???
	 * qq
	 * vcardmodel?????
	 * 
	 * ????58,????:????
	 * 	 * 
	 * */
    public VCardModel(Context context, Uri uri) throws MmsException {
        this(context, null, null, uri);
        initModelFromUri(uri);
        checkContentRestriction();
    }
	/**
	 * ??ANT???
	 * qq
	 * vcardmodel?????
	 * 
	 * ????58,????:????
	 * 	 * 
	 * */
    public VCardModel(Context context, String contentType, String src, Uri uri) throws MmsException {
        super(context, SmilHelper.ELEMENT_TAG_VCARD, contentType, src, uri);
        mExtras = new HashMap<String, String>();
    }
	/**
	 * ??ANT???
	 * qq
	 * ?uri?????vcardmodel
	 * 
	 * ????58,????:????
	 * 	 * 
	 * */
    private void initModelFromUri(Uri uri) throws MmsException {
        ContentResolver cr = mContext.getContentResolver();
        Cursor c = SqliteWrapper.query(mContext, cr, uri, null, null, null, null);

        
        boolean isFromMms = isMmsUri(uri); 
        if (isFromMms){
        	
        	if (c != null){
        		try {
        			if (c.moveToFirst()) {
        				
        				
        				  String data1 =c.getString(c.getColumnIndexOrThrow("_data"));
                        
                        
                    String name= MessageUtils.extractEncString(c.getString(c.getColumnIndexOrThrow("name")),106);
        				
        				
                    if ((name == null) || (TextUtils.isEmpty(name)))
                      {
                       String str1 = MessageUtils.extractEncString(c.getString(c.getColumnIndexOrThrow("cl")), 106);
                        boolean bool = str1.startsWith("cid:");
                        if (bool)
                        {
                          String str2 = str1.substring("cid:".length());
                         
                          name = str2;
                        }
                      }else{
                      	
                      	if (!name.endsWith(".vcf"))
                      		  this.mSrc = (name + ".vcf");
                      	
                      	this.mContentType = c.getString(c.getColumnIndexOrThrow("ct"));
                      	 if (TextUtils.isEmpty(mContentType)) {
                               throw new MmsException("Type of media is unknown.");
                           }
      
                      }
                          
                          
                          
//                         
//                      mExtras.put("album", "北邮");
//                      mExtras.put("artist", "曲凯明");
//                    
//                    
                    
        			}else {
                      throw new MmsException("Nothing found: " + uri);
                      }
        		}
        		
        		
        		finally{
        			
        			c.close();
        		}
        	} else {
              throw new MmsException("Bad URI: " + uri);
              }

              initMediaDuration();
        	
        	
        	
        	
        	
        	
        }else{
//      mExtras.put("album", "");
//      mExtras.put("artist", "");
//      
      String mName=uri.getPath();
      Log.v(TAG, "mName: "+mName);
      try{
      mName=mName.substring(mName.indexOf('-') + 1);
      mName=mName.substring(0,mName.indexOf('-'))+".vcf";
      }catch(Exception e){
    	  e.printStackTrace();
    	  
    	  mName=mName.substring(mName.indexOf('/') + 1)+".vcf";
    	  
      }
      Log.v(TAG, "mName: "+mName);
     // mName=MessageUtils.extractEncString(mName,106);
      Log.v(TAG, "mName: "+mName);
      this.mSrc =mName; 
      this.mContentType = "text/x-vcard";
        
//        if (c != null) {
//            try {
//                if (c.moveToFirst()) {
//                    String path;
//                    boolean isFromMms = isMmsUri(uri);
//
//                    // FIXME We suppose that there should be only two sources
//                    // of the audio, one is the media store, the other is
//                    // our MMS database.
////                    if (isFromMms) {
////                        path = c.getString(c.getColumnIndexOrThrow(Part._DATA));
////                        mContentType = c.getString(c.getColumnIndexOrThrow(Part.CONTENT_TYPE));
////                    } else {
//                    
//                    
//                    
//                         
//                    String data1 =c.getString(c.getColumnIndexOrThrow("_data"));
//                    
//                    
//                String name=    D(c.getString(c.getColumnIndexOrThrow("name")), 106);
//                    
//                if ((name == null) || (TextUtils.isEmpty(name)))
//                {
//                 String str1 = MessageUtils.extractEncString(c.getString(c.getColumnIndexOrThrow("cl")), 106);
//                  boolean bool = str1.startsWith("cid:");
//                  if (bool)
//                  {
//                    String str2 = str1.substring("cid:".length());
//                   
//                    name = str2;
//                  }
//                }else{
//                	
//                	if (!name.endsWith(".vcf"))
//                		  this.mSrc = (name + ".vcf");
//                	
//                	this.mContentType = c.getString(c.getColumnIndexOrThrow("ct"));
//                	 if (TextUtils.isEmpty(mContentType)) {
//                         throw new MmsException("Type of media is unknown.");
//                     }
//
//                }
//                    
//                    
//                    
//                   
//                mExtras.put("album", "北邮");
//                mExtras.put("artist", "曲凯明");
//                    
//                    
//                    
//                    
////                    path = c.getString(c.getColumnIndexOrThrow(Audio.Media.DATA));
////                        mContentType = c.getString(c.getColumnIndexOrThrow(
////                                Audio.Media.MIME_TYPE));
////                        // Get more extras information which would be useful
////                        // to the user.
////                        String album = c.getString(c.getColumnIndexOrThrow("album"));
////                        if (!TextUtils.isEmpty(album)) {
////                            mExtras.put("album", album);
////                        }
////
////                        String artist = c.getString(c.getColumnIndexOrThrow("artist"));
////                        if (!TextUtils.isEmpty(artist)) {
////                            mExtras.put("artist", artist);
////                        }
////                 //   }
////                    mSrc = path.substring(path.lastIndexOf('/') + 1);
////
////                   
//                    if (LOCAL_LOGV) {
//                        Log.v(TAG, "New AudioModel created:"
//                                + " mSrc=" + mSrc
//                                + " mContentType=" + mContentType
//                                + " mUri=" + uri
//                                + " mExtras=" + mExtras);
//                   }
//                } else {
//                    throw new MmsException("Nothing found: " + uri);
//                }
//            } finally {
//                c.close();
//            }
//        } else {
//            throw new MmsException("Bad URI: " + uri);
//        }

        initMediaDuration();
        }
    }

	/**
	 * ??ANT???
	 * qq
	 * ??vcard??
	 * 
	 * ????58,????:????
	 * 	 * 
	 * */

    public void stop() {
        appendAction(MediaAction.STOP);
        notifyModelChanged(false);
    }
	
		/**
	 * ??ANT???
	 * qq
	 * vcardmodel??
	 * 
	 * ????58,????:????
	 * 	 * 
	 * */

    public void handleEvent(Event evt) {
       

        notifyModelChanged(false);
    }

    public Map<String, ?> getExtras() {
        return mExtras;
    }
	/**
	 * ??ANT???
	 * qq
	 * ??vcard????
	 * 
	 * ????58,????:????
	 * 	 * 
	 * */
    protected void checkContentRestriction() throws ContentRestrictionException {
        ContentRestriction cr = ContentRestrictionFactory.getContentRestriction();
        cr.checkVCardContentType(mContentType);
    }

    	/**
	 * ??ANT???
	 * qq
	 * ?vcardmode?????0
	 * 
	 * ????58,????:????
	 * 	 * 
	 * */
    protected void initMediaDuration() throws MmsException {
    	
    	
    	super.mDuration=0;
    }
    @Override
    protected boolean isPlayable() {
        return true;
    }
}
