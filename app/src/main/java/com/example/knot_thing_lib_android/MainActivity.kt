package com.example.knot_thing_lib_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.knot_thing.KNoTAMQPFactory
import com.example.knot_thing.KNoTMessager
import com.example.knot_thing_lib_android.KNoTControlMessages.KNoTThingRegister
import kotlinx.android.synthetic.main.activity_main.register_button
import org.jetbrains.anko.doAsync

class MainActivity : AppCompatActivity() {
    val HOSTNAME = "192.168.31.61"
    val PORT_NUMBER = 5672

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val kNoTAMQP = KNoTAMQP("lucas", "lucas", HOSTNAME, PORT_NUMBER)
        lateinit var kNoTMessager : KNoTMessager
        val setKNoTMessager = { kNoTMessagerAux : KNoTMessager -> kNoTMessager = kNoTMessagerAux }
        KNoTAMQPFactory().getKNoTProtocolMessager(kNoTAMQP, setKNoTMessager)
        val kNoTThingRegister = KNoTThingRegister("1h3kkx1gi13idqe", "pocophone-lucas")

        register_button.setOnClickListener {
            doAsync { kNoTMessager.register(kNoTThingRegister) }
        }
    }
}
