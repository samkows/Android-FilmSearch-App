package com.example.skillcinema.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillcinema.data.Repository
import com.example.skillcinema.domain.HomeAndListPageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    repository: Repository
) : ViewModel() {

    private val useCase = HomeAndListPageUseCase(repository)

    private val _isLoading = MutableStateFlow<HomeLoadState>(HomeLoadState.Loading)
    val isLoading = _isLoading.asStateFlow()

//    private val _popular = MutableLiveData<ShortFilmDataListDto>()
//    val popular: LiveData<ShortFilmDataListDto> = _popular
//
//    private val _premieres = MutableLiveData<ShortFilmDataListDto>()
//    val premieres: LiveData<ShortFilmDataListDto> = _premieres
//
//    private val _top250 = MutableLiveData<ShortFilmDataListDto>()
//    val top250: LiveData<ShortFilmDataListDto> = _top250
//
//    private val _serials = MutableLiveData<ShortFilmDataListDto>()
//    val serials: LiveData<ShortFilmDataListDto> = _serials
//
//    private val _firstDinamic = MutableLiveData<ShortFilmDataListDto>()
//    val firstDynamic: LiveData<ShortFilmDataListDto> = _firstDinamic
//
//    private val _secondDinamic = MutableLiveData<ShortFilmDataListDto>()
//    val secondDynamic: LiveData<ShortFilmDataListDto> = _secondDinamic

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = HomeLoadState.Loading
            runCatching {
                val premieres = useCase.getPremieres()
                val popular = useCase.getPopularData()
                val top250 = useCase.getTop250()
                val serials = useCase.getSerials()
                val dynamicLists = useCase.getDynamicLists()
                val firstDynamic = dynamicLists[0]
                val secondDynamic = dynamicLists[1]
                val firstDynamicListIds = useCase.firstDynamicIds
                val secondDynamicListIds = useCase.secondDynamicIds

                _isLoading.value = HomeLoadState.Success(
                    premieres,
                    popular,
                    top250,
                    serials,
                    firstDynamic,
                    secondDynamic,
                    firstDynamicListIds,
                    secondDynamicListIds
                )
            }.onFailure { _isLoading.value = HomeLoadState.Error(it) }
        }
    }
}