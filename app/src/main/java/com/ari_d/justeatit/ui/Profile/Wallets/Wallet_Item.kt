package com.ari_d.justeatit.ui.Profile.Wallets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ari_d.justeatit.data.entities.Wallet
import com.ari_d.justeatit.other.walletEvent
import com.ari_d.justeatit.R

@Composable
fun WalletItem(
    wallet: Wallet,
    onEvent: (walletEvent) -> Unit,
    modifier: Modifier
) {
    Row (
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
            ){
        Column (
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
                ){
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = wallet.cardName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(
                    checked = wallet.isDone,
                    onCheckedChange = { isChecked ->
                        onEvent(walletEvent.onDoneChange(wallet, isChecked))
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = wallet.cardNumber)
        }
        IconButton(onClick = {
            onEvent(walletEvent.onDeleteWalletClick(wallet))
        }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = ""
            )
        }
    }
}