package com.example.knot_thing_lib_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.knot_thing.KNoTAMQPFactory
import com.example.knot_thing.KNoTMessager
import com.example.knot_thing.KNoTTypes.KNOT_TYPE_ID_SWITCH
import com.example.knot_thing.KNoTTypes.KNOT_UNIT_NOT_APPLICABLE
import com.example.knot_thing.KNoTTypes.KNOT_VALUE_TYPE_BOOL
import com.example.knot_thing_lib_android.KNoTControlMessages.KNoTThingAuth
import com.example.knot_thing_lib_android.KNoTControlMessages.KNoTThingRegister
import com.example.knot_thing_lib_android.KNoTControlMessages.KNoTThingSchema
import com.example.knot_thing_lib_android.KNoTControlMessages.KNoTThingUpdateSchema
import com.example.knot_thing_lib_android.KNoTControlMessages.KNoTThingUnregister
import com.example.knot_thing_lib_android.KNoTControlMessages.KNoTThingData
import com.example.knot_thing_lib_android.KNoTControlMessages.KNoTThingUpdateData
import kotlinx.android.synthetic.main.activity_main.register_button
import kotlinx.android.synthetic.main.activity_main.unregister_button
import kotlinx.android.synthetic.main.activity_main.authenticate_button
import kotlinx.android.synthetic.main.activity_main.schema_update_button
import kotlinx.android.synthetic.main.activity_main.data_publish_button
import org.jetbrains.anko.doAsync

class MainActivity : AppCompatActivity() {
    val HOSTNAME = "192.168.31.61"
    val PORT_NUMBER = 5672
    val THING_ID = "a74151d19de59cd3"
    val THING_NAME = "pocophone-lucas"
    val SENSOR_ID = 1
    val KNoT_VALUE_TYPE = KNOT_VALUE_TYPE_BOOL
    val KNoT_UNIT = KNOT_UNIT_NOT_APPLICABLE
    val KNoT_TYPE_ID = KNOT_TYPE_ID_SWITCH

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val kNoTAMQP = KNoTAMQP("lucas", "lucas", HOSTNAME, PORT_NUMBER)
        lateinit var kNoTMessager : KNoTMessager
        val setKNoTMessager = { kNoTMessagerAux : KNoTMessager -> kNoTMessager = kNoTMessagerAux }
        KNoTAMQPFactory().getKNoTProtocolMessager(kNoTAMQP, setKNoTMessager)
        val kNoTThingRegister = KNoTThingRegister(THING_ID, THING_NAME)
        val kNoTThingUnregister = KNoTThingUnregister(THING_ID)
        val kNoTThingAuth = KNoTThingAuth(THING_ID, "ejfhwekhrui234huirh23uf")
        // TODO: Add verification to check if unit, value_type and type_id respect the KNoT Protocol
        val kNoTThingSchema = mutableListOf(KNoTThingSchema(SENSOR_ID,
            KNoT_VALUE_TYPE,
            KNoT_UNIT,
            KNoT_TYPE_ID,
            "test")
        )

        val kNoTThingUpdateSchema = KNoTThingUpdateSchema(THING_ID, kNoTThingSchema)
        val knotData = mutableListOf(KNoTThingData(SENSOR_ID,true),
            KNoTThingData(SENSOR_ID,false),
            KNoTThingData(SENSOR_ID,true)
        )

        val kNoTThingUpdateData = KNoTThingUpdateData(THING_ID, knotData)

        register_button.setOnClickListener {
            doAsync { kNoTMessager.register(kNoTThingRegister) }
        }

        unregister_button.setOnClickListener {
            doAsync { kNoTMessager.unregister(kNoTThingUnregister) }
        }

        authenticate_button.setOnClickListener {
            doAsync { kNoTMessager.authenticate(kNoTThingAuth) }
        }

        schema_update_button.setOnClickListener {
            doAsync { kNoTMessager.updateSchema(kNoTThingUpdateSchema) }
        }

        data_publish_button.setOnClickListener {
            doAsync { kNoTMessager.publishData(kNoTThingUpdateData) }
        }

    }
}
