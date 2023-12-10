package com.example.appbike.view

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appbike.R
import com.example.appbike.model.PaymentModel
import com.example.appbike.presenter.PaymentPresenter

class PaymentActivity : AppCompatActivity(), PaymentView {

    private lateinit var presenter: PaymentPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        val payButton = findViewById<Button>(R.id.payButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        val textoHTML = resources.getString(R.string.subscribe_text)
        val bodyextView = findViewById<TextView>(R.id.textView10)
        val textoFormateado = Html.fromHtml(textoHTML)
        bodyextView.text = textoFormateado
        titleTextView.text = "Pagar Suscripción"

        val model = PaymentModel()
        presenter = PaymentPresenter(model, this)

        payButton.setOnClickListener {
            presenter.paySubscription()
        }

        cancelButton.setOnClickListener {
            presenter.cancelPayment()
        }
    }

    override fun showPaymentSuccess() {
        Toast.makeText(this, "Pago realizado con éxito", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    override fun navigateToProfile() {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }
}
