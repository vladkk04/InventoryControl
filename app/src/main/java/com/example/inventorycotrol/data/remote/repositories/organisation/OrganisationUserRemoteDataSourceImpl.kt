package com.example.inventorycotrol.data.remote.repositories.organisation

import com.example.inventorycotrol.common.ApiResponseResult
import com.example.inventorycotrol.data.constants.AppConstants
import com.example.inventorycotrol.data.constants.AppConstants.SELECTED_ORGANISATION_ID
import com.example.inventorycotrol.data.remote.dto.OrganisationUserDto
import com.example.inventorycotrol.data.remote.services.OrganisationUserApiService
import com.example.inventorycotrol.domain.manager.DataStoreManager
import com.example.inventorycotrol.domain.model.organisation.invitations.OrganisationInvitationEmailRequest
import com.example.inventorycotrol.domain.model.organisation.invitations.OrganisationInvitationUserIdRequest
import com.example.inventorycotrol.domain.model.organisation.user.OrganisationUserAssignRoleRequest
import com.example.inventorycotrol.domain.model.organisation.user.OrganisationUserUpdateRequest
import com.example.inventorycotrol.domain.repository.remote.OrganisationUserRemoteDataSource
import com.example.inventorycotrol.ui.utils.extensions.safeApiCallFlow
import com.example.inventorycotrol.ui.utils.extensions.safeSuspendResponseApiCallFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class OrganisationUserRemoteDataSourceImpl(
    private val api: OrganisationUserApiService,
    private val dataStoreManager: DataStoreManager,
) : OrganisationUserRemoteDataSource {

    private suspend fun organisationId() =
        dataStoreManager.getPreference(SELECTED_ORGANISATION_ID).firstOrNull()
            ?: throw Exception("Organisation id not found")

    private suspend fun userId() =
        dataStoreManager.getPreference(AppConstants.USER_ID_KEY).firstOrNull()
            ?: throw Exception("User id not found")

    override suspend fun update(
        organisationUserId: String,
        request: OrganisationUserUpdateRequest
    ): Flow<ApiResponseResult<Unit>> {
        return safeApiCallFlow { api.update(organisationId(), organisationUserId, request) }
    }

    override suspend fun assignRole(
        organisationUserId: String,
        request: OrganisationUserAssignRoleRequest
    ): Flow<ApiResponseResult<Unit>> {
        return safeApiCallFlow { api.assignRole(organisationId(), organisationUserId, request) }
    }

    override suspend fun getAll(): Flow<ApiResponseResult<List<OrganisationUserDto>>> {
        return safeSuspendResponseApiCallFlow { api.getAll(organisationId()) }
    }

    override suspend fun getByUserId(): Flow<ApiResponseResult<OrganisationUserDto>> {
        return safeSuspendResponseApiCallFlow { api.getByUserId(organisationId(), userId()) }
    }

    override suspend fun delete(organisationUserId: String): Flow<ApiResponseResult<Unit>> {
        return safeApiCallFlow { api.delete(organisationId(), organisationUserId) }
    }

    override suspend fun makeUserActive(organisationUserId: String): Flow<ApiResponseResult<Unit>> {
        return safeApiCallFlow { api.makeUserActive(organisationId(), organisationUserId) }
    }

    override suspend fun makeUserInactive(organisationUserId: String): Flow<ApiResponseResult<Unit>> {
        return safeApiCallFlow { api.makeUserInactive(organisationId(), organisationUserId) }
    }

    override suspend fun cancelInviteByUserId(userId: String): Flow<ApiResponseResult<Unit>> {
        return safeApiCallFlow { api.cancelInviteByUserId(organisationId(), userId) }
    }

    override suspend fun cancelInviteByUserEmail(email: String): Flow<ApiResponseResult<Unit>> {
        return safeApiCallFlow { api.cancelInviteByUserEmail(organisationId(), email) }
    }

    override suspend fun inviteUserByUserId(request: OrganisationInvitationUserIdRequest): Flow<ApiResponseResult<OrganisationUserDto>> {
        return safeSuspendResponseApiCallFlow {
            api.inviteUserByUserId(organisationId(), request)
        }
    }

    override suspend fun inviteUserByEmail(request: OrganisationInvitationEmailRequest): Flow<ApiResponseResult<OrganisationUserDto>> {
        return safeSuspendResponseApiCallFlow {
            api.inviteUserByEmail(organisationId(), request)
        }
    }

}