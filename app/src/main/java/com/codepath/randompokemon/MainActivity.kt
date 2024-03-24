package com.codepath.randompokemon

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var nameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton: Button = findViewById(R.id.searchPokemonButton)
        val randomButton: Button = findViewById(R.id.pokemonButton)
        val imageView: ImageView = findViewById(R.id.pokemonImage)
        val editText: EditText = findViewById(R.id.pokemonNameEditText)
        nameTextView = findViewById(R.id.pokemonNameTextView)

        searchButton.setOnClickListener {
            val pokemonName = editText.text.toString().trim().lowercase(Locale.getDefault())
            if (pokemonName.isNotEmpty()) {
                getPokemon(pokemonName, imageView)
            } else {
                editText.error = "Please enter a Pokémon name"
            }
        }

        randomButton.setOnClickListener {
            editText.text.clear()
            getRandomPokemonImage(imageView)
        }
    }

    private fun getPokemon(pokemonName: String, imageView: ImageView) {
        val client = AsyncHttpClient()
        client.get("https://pokeapi.co/api/v2/pokemon/$pokemonName", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                updateUIWithPokemonData(json, imageView)
            }

            override fun onFailure(
                statusCode: Int, headers: Headers?, errorResponse: String?, throwable: Throwable?
            ) {
                Log.d("Pokemon Error", "Failed to fetch Pokemon image: $errorResponse")
            }
        })
    }

    private fun getRandomPokemonImage(imageView: ImageView) {
        val randomPokemonId = (1..1025).random()
        val client = AsyncHttpClient()
        client.get("https://pokeapi.co/api/v2/pokemon/$randomPokemonId", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                updateUIWithPokemonData(json, imageView)
            }

            override fun onFailure(
                statusCode: Int, headers: Headers?, errorResponse: String?, throwable: Throwable?
            ) {
                Log.d("Pokemon Error", "Failed to fetch Pokemon image: $errorResponse")
            }
        })
    }

    private fun updateUIWithPokemonData(json: JsonHttpResponseHandler.JSON, imageView: ImageView) {
        val spriteUrl = json.jsonObject.getJSONObject("sprites").getString("front_default")
        val pokemonName = json.jsonObject.getString("name").replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        Glide.with(this@MainActivity)
            .load(spriteUrl)
            .fitCenter()
            .into(imageView)

        nameTextView.text = pokemonName
    }
}