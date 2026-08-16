package cat.merce.comunicador

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cat.merce.comunicador.ui.MainActivity

/**
 * Reopens the app after the device restarts, when a helper has asked for it.
 *
 * This matters more than it sounds. She cannot tap an icon: if the tablet
 * reboots overnight and stops at the home screen, she has no voice until
 * somebody notices and opens the app for her.
 *
 * Android restricts starting a screen from the background, so this is not
 * guaranteed on every device. The dependable route is to make the app the
 * home screen as well, which the manifest allows, so that a restart lands in
 * the app because the app *is* what the device opens. This receiver is the
 * belt to that pair of braces, and does nothing if the setting is off.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            return
        }

        val settings = context.getSharedPreferences(
            MainActivity.SETTINGS_FILE, Context.MODE_PRIVATE
        )
        if (!settings.getBoolean(MainActivity.KEY_OPEN_ON_BOOT, false)) return

        val open = Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // Blocked on some versions by the background-start rules; the home
        // screen route is what makes it reliable, so failing here is survivable.
        runCatching { context.startActivity(open) }
    }
}
