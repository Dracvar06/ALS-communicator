package cat.merce.comunicador.ui

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import cat.merce.comunicador.R

/**
 * Atkinson Hyperlegible, from the Braille Institute, under the SIL Open Font
 * License. See OFL-AtkinsonHyperlegible.txt at the root of this project.
 *
 * It was drawn for readers with low vision: the letters that usually collapse
 * into each other at a glance, capital I against lower case l against 1, and O
 * against 0, are deliberately given different shapes. Everything here is read
 * at speed, out of the corner of the eye, under a moving highlight, which is
 * exactly the case it was designed for.
 */
val Hyperlegible = FontFamily(
    Font(R.font.atkinson_hyperlegible_regular, FontWeight.Normal),
    Font(R.font.atkinson_hyperlegible_bold, FontWeight.Bold),
)
