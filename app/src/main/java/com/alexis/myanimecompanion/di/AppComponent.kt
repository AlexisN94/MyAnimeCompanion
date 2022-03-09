package com.alexis.myanimecompanion.di

import com.alexis.myanimecompanion.ui.details.DetailsFragment
import com.alexis.myanimecompanion.ui.edit.EditFragment
import com.alexis.myanimecompanion.ui.list.ListFragment
import com.alexis.myanimecompanion.ui.profile.ProfileFragment
import com.alexis.myanimecompanion.ui.search.SearchFragment
import dagger.Component

@Component(modules = [DataSourceModule::class, RepositoryModule::class, ContextModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(
            dataSourceModule: DataSourceModule,
            repositoryModule: RepositoryModule,
            contextModule: ContextModule
        ): AppComponent
    }

    fun inject(fragment: ProfileFragment)
    fun inject(fragment: ListFragment)
    fun inject(fragment: DetailsFragment)
    fun inject(fragment: EditFragment)
    fun inject(fragment: SearchFragment)
}
