package com.example.inventorycotrol.domain.usecase.organisatonSettings

import com.example.inventorycotrol.domain.model.organisation.settings.OrganisationSettingsRequest
import com.example.inventorycotrol.domain.repository.remote.OrganisationSettingsRemoteDataSource

class UpdateOrganisationSettingsUseCase(
    private val remote: OrganisationSettingsRemoteDataSource
) {
    suspend operator fun invoke(request: OrganisationSettingsRequest) = remote.update(request)

}
