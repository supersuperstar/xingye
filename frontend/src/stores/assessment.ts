import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { assessmentApi } from '@/api/assessment'
import type {
  RiskAssessment,
  RiskAssessmentDto,
  RiskAssessmentResultDto,
  AssessmentStatus,
  RiskLevel,
  AssessmentStatistics
} from '@/api/types'

export const useAssessmentStore = defineStore('assessment', () => {
  // 状态
  const currentAssessment = ref<RiskAssessment | null>(null)
  const assessmentList = ref<RiskAssessment[]>([])
  const statistics = ref<AssessmentStatistics | null>(null)
  const loading = ref(false)

  // 计算属性
  const pendingAssessments = computed(() =>
    assessmentList.value.filter(assessment => assessment.status === AssessmentStatus.PENDING)
  )

  const underReviewAssessments = computed(() =>
    assessmentList.value.filter(assessment => assessment.status === AssessmentStatus.UNDER_REVIEW)
  )

  const approvedAssessments = computed(() =>
    assessmentList.value.filter(assessment => assessment.status === AssessmentStatus.APPROVED)
  )

  const assessmentsByRiskLevel = computed(() => {
    const grouped: Record<RiskLevel, RiskAssessment[]> = {
      [RiskLevel.CONSERVATIVE]: [],
      [RiskLevel.MODERATE]: [],
      [RiskLevel.AGGRESSIVE]: []
    }

    assessmentList.value.forEach(assessment => {
      grouped[assessment.riskLevel].push(assessment)
    })

    return grouped
  })

  // Actions
  const submitAssessment = async (data: RiskAssessmentDto) => {
    loading.value = true
    try {
      const response = await assessmentApi.submit(data)
      currentAssessment.value = response.data.assessment
      return response.data
    } finally {
      loading.value = false
    }
  }

  const getAssessmentById = async (id: number) => {
    loading.value = true
    try {
      const response = await assessmentApi.getById(id)
      return response.data
    } finally {
      loading.value = false
    }
  }

  const getCustomerAssessments = async (customerId: number) => {
    loading.value = true
    try {
      const response = await assessmentApi.getByCustomerId(customerId)
      assessmentList.value = response.data
      return response.data
    } finally {
      loading.value = false
    }
  }

  const getLatestAssessment = async (customerId: number) => {
    loading.value = true
    try {
      const response = await assessmentApi.getLatestByCustomerId(customerId)
      currentAssessment.value = response.data
      return response.data
    } finally {
      loading.value = false
    }
  }

  const recalculateRiskScore = async (id: number) => {
    loading.value = true
    try {
      const response = await assessmentApi.recalculate(id)
      // 更新当前评估
      if (currentAssessment.value?.id === id) {
        currentAssessment.value = response.data.assessment
      }
      // 更新列表中的评估
      const index = assessmentList.value.findIndex(a => a.id === id)
      if (index !== -1) {
        assessmentList.value[index] = response.data.assessment
      }
      return response.data
    } finally {
      loading.value = false
    }
  }

  const getAssessmentsByRiskLevel = async (riskLevel: RiskLevel) => {
    loading.value = true
    try {
      const response = await assessmentApi.getByRiskLevel(riskLevel)
      return response.data
    } finally {
      loading.value = false
    }
  }

  const getAssessmentsByStatus = async (status: AssessmentStatus) => {
    loading.value = true
    try {
      const response = await assessmentApi.getByStatus(status)
      return response.data
    } finally {
      loading.value = false
    }
  }

  const getPendingAssessments = async () => {
    loading.value = true
    try {
      const response = await assessmentApi.getPending()
      assessmentList.value = response.data
      return response.data
    } finally {
      loading.value = false
    }
  }

  const getUnderReviewAssessments = async () => {
    loading.value = true
    try {
      const response = await assessmentApi.getUnderReview()
      assessmentList.value = response.data
      return response.data
    } finally {
      loading.value = false
    }
  }

  const updateAssessmentStatus = async (id: number, status: AssessmentStatus) => {
    loading.value = true
    try {
      const response = await assessmentApi.updateStatus(id, status)
      // 更新当前评估
      if (currentAssessment.value?.id === id) {
        currentAssessment.value = response.data
      }
      // 更新列表中的评估
      const index = assessmentList.value.findIndex(a => a.id === id)
      if (index !== -1) {
        assessmentList.value[index] = response.data
      }
      return response.data
    } finally {
      loading.value = false
    }
  }

  const getStatistics = async () => {
    loading.value = true
    try {
      const response = await assessmentApi.getStatistics()
      statistics.value = response.data
      return response.data
    } finally {
      loading.value = false
    }
  }

  const calculateRiskScore = async (questionnaireAnswers: Record<string, any>) => {
    loading.value = true
    try {
      const response = await assessmentApi.calculateScore(questionnaireAnswers)
      return response.data
    } finally {
      loading.value = false
    }
  }

  const setCurrentAssessment = (assessment: RiskAssessment | null) => {
    currentAssessment.value = assessment
  }

  const clearCurrentAssessment = () => {
    currentAssessment.value = null
  }

  const clearAssessmentList = () => {
    assessmentList.value = []
  }

  const clearStatistics = () => {
    statistics.value = null
  }

  return {
    // 状态
    currentAssessment,
    assessmentList,
    statistics,
    loading,

    // 计算属性
    pendingAssessments,
    underReviewAssessments,
    approvedAssessments,
    assessmentsByRiskLevel,

    // Actions
    submitAssessment,
    getAssessmentById,
    getCustomerAssessments,
    getLatestAssessment,
    recalculateRiskScore,
    getAssessmentsByRiskLevel,
    getAssessmentsByStatus,
    getPendingAssessments,
    getUnderReviewAssessments,
    updateAssessmentStatus,
    getStatistics,
    calculateRiskScore,
    setCurrentAssessment,
    clearCurrentAssessment,
    clearAssessmentList,
    clearStatistics
  }
})
