package com.ari_d.justeatit.ui.Profile.Wallets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ari_d.justeatit.data.entities.Wallet
import com.ari_d.justeatit.other.walletEvent
import com.ari_d.justeatit.R

@Composable
fun CreditCardItem(
    wallet: Wallet,
    onEvent: (walletEvent) -> Unit,
    modifier: Modifier,
    imagePainter: Painter
) {
    Card(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        shape = RoundedCornerShape(8.dp),
        elevation = 5.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(12.dp)
        ) {
            Image(
                painter = imagePainter,
                contentDescription = "Credit Card",
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            )
            Column(
                Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxHeight()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = wallet.cardName,
                        fontSize = 20.sp,
                        color = Color(0xFFFFFFFF)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = wallet.cardNumber
                            .substring(0, 4) + " " + "****" + " " + "****" + " " + "****",
                        fontSize = 28.sp,
                        color = Color(0xFFFFFFFF)
                    )
                }
            }
            Box (
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ){
                IconButton(
                    onClick = {
                        onEvent(walletEvent.onDeleteWalletClick(wallet))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
        }
    }
}