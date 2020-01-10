package jp.ac.nkc.kadai04_ct02

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        HitAPITask().execute("http://weather.livedoor.com/forecast/webservice/json/v1?city=230010")

        tokyoButton.setOnClickListener {
            HitAPITask().execute("http://weather.livedoor.com/forecast/webservice/json/v1?city=130010")
        }
        nagoyaButton.setOnClickListener {
            HitAPITask().execute("http://weather.livedoor.com/forecast/webservice/json/v1?city=230010")
        }
        osakaButton.setOnClickListener {
            HitAPITask().execute("http://weather.livedoor.com/forecast/webservice/json/v1?city=270000")
        }
    }

    //nagoya=230010 osaka=270000 tokyo=130010

    inner class HitAPITask : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String? {
            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null
            val buffer: StringBuffer
            try {
                val url = URL(params[0])
                connection = url.openConnection() as HttpURLConnection
                connection.connect() //ここで指定した API を叩いてみてます。
                val stream = connection.inputStream
                reader = BufferedReader(InputStreamReader(stream))
                buffer = StringBuffer()
                var line: String?
                while (true) {
                    line = reader.readLine()
                    if (line == null) {
                        break
                    }
                    buffer.append(line)
                }
                val jsonText = buffer.toString()

                return jsonText

            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
                try {
                    reader?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return null
        }
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result == null) return

            val parentJsonObj = JSONObject(result)
            val forecasts = parentJsonObj.getJSONArray("forecasts")
            val arraySize = forecasts.length()

            val today = forecasts.getJSONObject(0)
            val tomorrow = forecasts.getJSONObject(1)

            if (arraySize == 3){
                val dat = forecasts.getJSONObject(2)
                datText.text = dat.getString("telop")
            }else{
                datText.text = "─"
            }

            val detail = parentJsonObj.getJSONObject("description")

            areaText.text = parentJsonObj.getString("title")
            todayText.text = today.getString("telop")
            tomorrowText.text = tomorrow.getString("telop")

            detailText.text = detail.getString("text")
        }
    }
}
