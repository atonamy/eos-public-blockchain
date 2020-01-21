package one.block.eospublicblockchain.data_binding

import androidx.databinding.BindingAdapter
import com.github.curioustechizen.ago.RelativeTimeTextView
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

@BindingAdapter("date")
fun setDate(view: RelativeTimeTextView, date: LocalDateTime?) {
    if(date != null)
        view.setReferenceTime(date.toInstant(ZoneOffset.UTC).toEpochMilli())
}