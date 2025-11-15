package com.example.iperf3client.utils

class IpersOutputParser {

    /*
regex: \[[0-9]+\]\s+[0-9]*\.[0-9]+-[0-9]*\.[0-9]+\s+sec\s+(?<transfer>[0-9]*\.[0-9])+\s+MBytes\s+(?<bw>[0-9]*\.[0-9])+\s+Mbits/sec\s+receiver
example: [127]   0.00-5.00   sec  15.7 MBytes  26.3 Mbits/sec                  receiver
regex groups:
 -transfer = 15.7
 -bw = 26.3
*/


    companion object {
        private const val KBYTES = "KBytes"
        private const val KBITS_PER_SEC = "Kbits/sec"
        private const val BYTES = " Bytes "
        private const val BITS_PER_SEC = " bits/sec"
        private const val UPLOAD_EXTRA_VALUES_SEPARATOR =
            "/sec "  // upload has more values that interfere with the validation

        private fun getTransferOrBwValues(input: String, value: String, pattern: String): String {
            val regex = Regex(
                pattern = pattern,
                options = setOf(RegexOption.IGNORE_CASE)
            )

            try {
                val matchResult = regex.find(input)!!
                return matchResult.groups[value]?.value.toString()
            } catch (e: Exception) {
                return ""
            }

        }

        fun getFinalTransferOrBwValues(input: String, value: String): String {

            val pattern =
                "\\[[0-9]+\\]\\s+[0-9]*\\.[0-9]+-[0-9]*\\.[0-9]+\\s+sec\\s+(?<transfer>([+-]?(?=\\.\\d|\\d)(?:\\d+)?(?:\\.?\\d*))(?:[Ee]([+-]?\\d+))?+\\s+[A-Za-z]+)\\s+(?<bw>([+-]?(?=\\.\\d|\\d)(?:\\d+)?(?:\\.?\\d*))(?:[Ee]([+-]?\\d+))?+\\s+[A-Za-z]+)"
            return getTransferOrBwValues(input, value, pattern)
        }

        private fun getIntermediateTransferOrBwValues(input: String, value: String): String {
            val pattern =
                "\\[[0-9]+\\]\\s+[0-9]*\\.[0-9]+-[0-9]*\\.[0-9]+\\s+sec\\s+(?<transfer>([+-]?(?=\\.\\d|\\d)(?:\\d+)?(?:\\.?\\d*))(?:[Ee]([+-]?\\d+))?)\\s+(?<transferUnits>[A-Za-z]+)\\s+(?<bw>([+-]?(?=\\.\\d|\\d)(?:\\d+)?(?:\\.?\\d*))(?:[Ee]([+-]?\\d+))?) (?<bwUnits>[A-Za-z]+/[A-Za-z]+)"
            return getTransferOrBwValues(input, value, pattern)
        }


        fun getIntermediateTransferOrBwValuesInMBytes(input: String, value: String): Float {
            try {
                val value = getIntermediateTransferOrBwValues(input, value).toFloat()
                val inputFormatted = input.substring(
                    0,
                    input.indexOf(UPLOAD_EXTRA_VALUES_SEPARATOR) + UPLOAD_EXTRA_VALUES_SEPARATOR.length
                )
                if (inputFormatted.contains(KBYTES) || inputFormatted.contains(KBITS_PER_SEC)) {
                    return kiloToMega(value)
                } else if (inputFormatted.contains(BYTES) || inputFormatted.contains(BITS_PER_SEC)) {
                    return bitToMega(value)
                }

                return value
            } catch (e: Exception) {
                //return 0f //TODO: change this so there is no trailing 0s in the graph
                throw IllegalStateException("Iperf line is not a result or parsed wrong: $input")
            }
        }

        /*
        MBytes -->   KBytes
        Mbits/sec --> Kbits/sec
         */
        private fun kiloToMega(transfer: Float): Float {
            return transfer / 1000
        }

        //0.00 Bytes  0.00 bits/sec
        private fun bitToMega(transfer: Float): Float {
            return transfer / 1000000
        }


    }


}