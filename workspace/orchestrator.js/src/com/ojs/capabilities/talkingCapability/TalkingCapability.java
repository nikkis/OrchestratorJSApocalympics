package com.ojs.capabilities.talkingCapability;

import java.util.HashMap;

import org.json.JSONObject;

import com.ojs.OrchestratorJsActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;

@SuppressLint("NewApi") 
public class TalkingCapability {
	private TextToSpeech tts_;
	private boolean readyForUse_;
	private AudioManager audioManager_;
	private int defaultVolume_;
	private static Context applicationContext_;
	protected static boolean speechReady_ = true;
	
	public void initCapability( Context applicationContext ) {
		TalkingCapability.applicationContext_ = applicationContext;
		tts_ = new TextToSpeech( TalkingCapability.applicationContext_, new OnInitListener() {
			@Override
			public void onInit(int status) {
				readyForUse_ = true;
			}
		});
		
		tts_.setOnUtteranceProgressListener(new UtteranceProgressListener() {
			@Override
			public void onStart(String utteranceId) {}
			@Override
			public void onError(String utteranceId) { speechReady_ = true; }
			@Override
			public void onDone(String utteranceId) { speechReady_ = true; }
		});
		
		
		audioManager_ = (AudioManager)applicationContext_.getSystemService(Context.AUDIO_SERVICE);
		defaultVolume_ = audioManager_.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	public JSONObject say( String str, String filter, String pitch ) throws Exception {
		if( !readyForUse_ )
			throw new Exception( "TalkingCapability not yet initialized!" );
		
		syncSpeech(str, filter, pitch);
		Log.d(OrchestratorJsActivity.TAG, "finished");
		return null;
	}
	
	

	public JSONObject shout( String str, String filter, String pitch ) throws Exception {
		if( !readyForUse_ )
			throw new Exception( "TalkingCapability not yet initialized!" );
		
		audioManager_.setStreamVolume(audioManager_.STREAM_MUSIC, 
				audioManager_.getStreamMaxVolume(audioManager_.STREAM_MUSIC), 0);

		syncSpeech(str, filter, pitch);
		
		audioManager_.setStreamVolume(audioManager_.STREAM_MUSIC, defaultVolume_, 0);
		return null;
	}
	
	
	
	// wait until finishes or an error happens
	private void syncSpeech( String str, String filter, String pitch ) throws Exception {
		tts_.setPitch( Float.parseFloat( pitch ) );
		speechReady_ = false;
		HashMap<String, String> myHashAlarm = new HashMap<String, String>();
		myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
		myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");

		tts_.speak( str, TextToSpeech.QUEUE_FLUSH, myHashAlarm );
		
		while (!TalkingCapability.speechReady_) {
			Thread.sleep(500);
		}
		return;
	}
		
}
