@file:Suppress("SameReturnValue")

package id.usereal.storyapp.view.main

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import id.usereal.storyapp.DataDummy
import id.usereal.storyapp.MainDispatcherRule
import id.usereal.storyapp.data.local.preference.UserPreference
import id.usereal.storyapp.data.model.ListStoryItem
import id.usereal.storyapp.data.remote.ApiService
import id.usereal.storyapp.data.repository.StoryRepository
import id.usereal.storyapp.data.repository.UserRepository
import id.usereal.storyapp.getOrAwaitValue
import id.usereal.storyapp.view.MainAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var userPreference: UserPreference

    private lateinit var userRepository: UserRepository
    private lateinit var mockedLog: MockedStatic<Log>

    @Before
    fun setUp() {
        userRepository = UserRepository.getInstance(userPreference, apiService)

        mockedLog = Mockito.mockStatic(Log::class.java)
        mockedLog.`when`<Boolean> { Log.isLoggable(Mockito.anyString(), Mockito.anyInt()) }
            .thenReturn(false)
    }

    @Test
    fun `when get stories should not null and return data`() = runTest {
        val dummyStories = DataDummy.generateDummyStoryResponse()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStories)
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getAllStories()).thenReturn(expectedStory)

        val storyListViewModel = MainViewModel(storyRepository, userRepository)
        val actualStories: PagingData<ListStoryItem> = storyListViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getAllStories()).thenReturn(expectedStory)

        val storyListViewModel = MainViewModel(storyRepository, userRepository)
        val actualStory: PagingData<ListStoryItem> = storyListViewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = MainAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertEquals(0, differ.snapshot().size)
    }

    @After
    fun tearDown() {
        mockedLog.close()
    }
}

@Suppress("SameReturnValue")
class StoryPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(ignoredState: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}