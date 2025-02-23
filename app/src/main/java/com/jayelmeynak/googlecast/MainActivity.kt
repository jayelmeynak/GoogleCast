package com.jayelmeynak.googlecast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status

class MainActivity : AppCompatActivity() {
    private lateinit var castContext: CastContext
    private lateinit var sessionManagerListener: SessionManagerListener<CastSession>
    private var remoteMediaClient: RemoteMediaClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        castContext = CastContext.getSharedInstance(this)

        val mediaRouteButton = findViewById<MediaRouteButton>(R.id.mediaRouteButton)
        CastButtonFactory.setUpMediaRouteButton(this, mediaRouteButton)

        sessionManagerListener = object : SessionManagerListener<CastSession> {
            override fun onSessionStarting(session: CastSession) {}

            override fun onSessionStarted(session: CastSession, sessionId: String) {
                remoteMediaClient = session.remoteMediaClient
                remoteMediaClient?.let { loadMedia(it) }
            }

            override fun onSessionEnding(session: CastSession) {}

            override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
                remoteMediaClient = session.remoteMediaClient
            }

            override fun onSessionResumeFailed(session: CastSession, error: Int) {}

            override fun onSessionEnded(session: CastSession, error: Int) {
                remoteMediaClient = null
            }

            override fun onSessionStartFailed(session: CastSession, error: Int) {}
            override fun onSessionSuspended(session: CastSession, reason: Int) {}
            override fun onSessionResuming(session: CastSession, sessionId: String) {}
        }
    }

    override fun onResume() {
        super.onResume()
        castContext.sessionManager.addSessionManagerListener(
            sessionManagerListener,
            CastSession::class.java
        )
    }

    override fun onPause() {
        super.onPause()
        castContext.sessionManager.removeSessionManagerListener(
            sessionManagerListener,
            CastSession::class.java
        )
    }

    private fun loadMedia(remoteMediaClient: RemoteMediaClient) {
        val mediaInfo =
            com.google.android.gms.cast.MediaInfo.Builder("https://videolink-test.mycdn.me/?pct=1&sig=6QNOvp0y3BE&ct=0&clientType=45&mid=193241622673&type=5")
                .setContentType("video/mp4")
                .setStreamType(com.google.android.gms.cast.MediaInfo.STREAM_TYPE_BUFFERED)
                .build()

        remoteMediaClient.load(mediaInfo, true)
            .setResultCallback(object : ResultCallback<RemoteMediaClient.MediaChannelResult> {
                override fun onResult(result: RemoteMediaClient.MediaChannelResult) {
                    val status: Status = result.status
                }
            })
    }
}