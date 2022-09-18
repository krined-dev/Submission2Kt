package utils

enum class Filter {
    GAUSSIAN;
}


val applyFilter: (Png, Filter) -> Png = { img: Png, filter: Filter ->
    when (filter) {
        Filter.GAUSSIAN -> {
            img
        }
    }
}


