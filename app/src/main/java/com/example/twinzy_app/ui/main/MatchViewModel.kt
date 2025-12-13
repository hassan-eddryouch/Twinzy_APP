package com.example.twinzy_app.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.twinzy_app.data.model.Match
import com.example.twinzy_app.data.model.User
import com.example.twinzy_app.data.repository.MatchRepository
import com.example.twinzy_app.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll // Ajouté pour awaitAll()
import kotlinx.coroutines.coroutineScope // Ajouté pour le scope concurrent
import javax.inject.Inject

data class MatchWithUser(
    val match: com.example.twinzy_app.data.model.Match,
    val otherUser: com.example.twinzy_app.data.model.User
)

sealed class MatchesUiState {
    object Loading : MatchesUiState()
    data class Success(val matches: List<MatchWithUser>) : MatchesUiState()
    data class Error(val message: String) : MatchesUiState()
    object Empty : MatchesUiState()
}

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<MatchesUiState>(MatchesUiState.Loading)
    val uiState: StateFlow<MatchesUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadMatches()
    }

    fun loadMatches() {
        val currentUserId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.value = MatchesUiState.Loading

            matchRepository.observeMatches(currentUserId)
                .catch { e ->
                    // Gérer l'erreur de base de données ou de réseau
                    _uiState.value = MatchesUiState.Error(e.message ?: "Failed to load matches")
                }
                .collect { matches ->
                    if (matches.isEmpty()) {
                        _uiState.value = MatchesUiState.Empty
                    } else {
                        // FIX: Utiliser coroutineScope et awaitAll pour exécuter les requêtes en parallèle (rapidité)
                        // Ces requêtes peuvent retourner null (MatchWithUser?)
                        val matchesWithNullableUsers = coroutineScope {
                            matches.mapNotNull { match ->
                                val otherUserId = match.users.firstOrNull { it != currentUserId }

                                otherUserId?.let {
                                    async {
                                        userRepository.getUserById(it).getOrNull()?.let { user ->
                                            MatchWithUser(match, user)
                                        }
                                    }
                                }
                            }.awaitAll()
                        }

                        // TASHI7: Filtrer les résultats null pour obtenir List<MatchWithUser> non-nullable
                        val finalMatches = matchesWithNullableUsers.filterNotNull()

                        if (finalMatches.isEmpty()) {
                            _uiState.value = MatchesUiState.Empty
                        } else {
                            // Envoi de la liste corrigée List<MatchWithUser>
                            _uiState.value = MatchesUiState.Success(finalMatches)
                        }
                    }
                    _isRefreshing.value = false
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadMatches()
        }
    }

    fun unmatch(matchId: String) {
        viewModelScope.launch {
            matchRepository.unmatch(matchId)
                .onSuccess {
                    // Le Match sera retiré automatiquement par l'observateur Firestore
                }
                .onFailure { e ->
                    // Gérer l'erreur si l'unmatch échoue
                    // Vous pouvez ajouter ici un flow d'erreur pour les messages Toast ou Dialogs
                }
        }
    }
}