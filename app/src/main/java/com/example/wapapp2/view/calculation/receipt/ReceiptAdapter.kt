import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wapapp2.databinding.ViewReceiptItemBinding
import com.example.wapapp2.databinding.ViewRecentCalcItemBinding
import com.example.wapapp2.model.ReceiptDTO
import com.example.wapapp2.model.ReceiptProductDTO
import com.example.wapapp2.view.calculation.interfaces.OnUpdateSummaryCallback
import com.example.wapapp2.viewmodel.ReceiptViewModel
import org.joda.time.DateTime
import java.text.DecimalFormat

/** 영수증 Adapter **/
class ReceiptAdapter(private val context: Context?, private val receipts: ArrayList<ReceiptDTO>, receiptViewModel: ReceiptViewModel, onUpdateSummaryCallback: OnUpdateSummaryCallback)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val receiptViewModel = receiptViewModel
    val onUpdateSummaryCallback : OnUpdateSummaryCallback = onUpdateSummaryCallback

    inner class ReceiptVM(val binding: ViewReceiptItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(receipt: ReceiptDTO) {
            binding.description.text = "[ " + receipt.name + " ] - 김진우"
            binding.recentCalcItem.adapter = ReceiptItemAdapter(context, receipt.getProducts())
            binding.dateTime.text = DateTime.parse(receipt.date).toString("yyyy-MM-dd")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReceiptVM(ViewReceiptItemBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return (holder as ReceiptVM).bind(receipts[position])
    }

    override fun getItemCount(): Int {
        return receipts.size
    }

    /** 영수증 세부 항목 Adapter **/
    private inner class ReceiptItemAdapter(private val context: Context?, private val items: ArrayList<ReceiptProductDTO>)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        inner class ReceiptMenuVH(val binding: ViewRecentCalcItemBinding) : RecyclerView.ViewHolder(binding.root) {


            fun bind(product: ReceiptProductDTO) {
                var myPrice = try {
                    product.price / product.personCount
                } catch (e: ArithmeticException) {
                    0
                }

                binding.receiptMenu.text = product.name
                binding.receiptTotalMoney.text = DecimalFormat("#,###").format(product.price)
                binding.receiptMyMoney.text = DecimalFormat("#,###").format(myPrice)


                receiptViewModel.updateSummary_forNewProduct(product)

                binding.recentCalcCkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        receiptViewModel.product_checked(product)
                        binding.receiptMyMoney.text = DecimalFormat("#,###").format(myPrice)
                        binding.receiptPersonCount.text = product.personCount.toString() + "/3"
                        onUpdateSummaryCallback.updateSummaryUI(receiptViewModel.getCurrentSummary)
                    } else {
                        receiptViewModel.product_unchecked(product)
                        binding.receiptMyMoney.text = "0"
                        binding.receiptPersonCount.text = product.personCount.toString() + "/3"
                        onUpdateSummaryCallback.updateSummaryUI(receiptViewModel.getCurrentSummary)
                    }

                }

                binding.receiptPersonCount.text = "${product.personCount}/3"
                binding.recentCalcCkbox.isChecked = true
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ReceiptMenuVH(ViewRecentCalcItemBinding.inflate(LayoutInflater.from(context)))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            return (holder as ReceiptMenuVH).bind(items[position])
        }

        override fun getItemCount(): Int {
            return items.size
        }

    }

}

