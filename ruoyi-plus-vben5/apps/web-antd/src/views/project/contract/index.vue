<script setup lang="ts">
import type {
  ContractPaymentRecord,
  ContractPaymentStatCard,
} from '#/mock/modules/project/contract';

import { onMounted, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  PlatformStatCard,
  PlatformStatusTag,
  PlatformViewToolbar,
} from '@st/platform-ui';

import ContractPaymentMiniBar from './components/contract-payment-mini-bar.vue';
import {
  contractPaymentStatusMap,
  getContractPaymentList,
  getContractPaymentStats,
} from '#/mock/modules/project/contract';

const contractRows = ref<ContractPaymentRecord[]>([]);
const statCards = ref<ContractPaymentStatCard[]>([]);
const loading = ref(false);

onMounted(async () => {
  await loadContractPayments();
});

async function loadContractPayments() {
  loading.value = true;
  try {
    const [rows, stats] = await Promise.all([
      getContractPaymentList(),
      getContractPaymentStats(),
    ]);
    contractRows.value = rows;
    statCards.value = stats;
  } finally {
    loading.value = false;
  }
}

function formatAmount(value: number) {
  return `${value.toLocaleString()}万`;
}
</script>

<template>
  <Page auto-content-height>
    <div class="project-contract-page">
      <PlatformViewToolbar
        description="合同存储、版本控制、付款审批联动"
        title="合同与付款管理"
      />

      <section class="project-contract-stat-grid">
        <PlatformStatCard
          v-for="card in statCards"
          :key="card.title"
          :loading="loading"
          v-bind="card"
        />
      </section>

      <section class="project-contract-card-grid">
        <article
          v-for="contract in contractRows"
          :key="contract.id"
          class="project-contract-card"
        >
          <header class="project-contract-card__header">
            <h2>{{ contract.name }}</h2>
            <PlatformStatusTag
              :label="contractPaymentStatusMap[contract.status].label"
              :status="contractPaymentStatusMap[contract.status].status"
            />
          </header>

          <div class="project-contract-card__summary">
            <span>合同金额</span>
            <strong>{{ formatAmount(contract.contractAmount) }}</strong>
          </div>

          <div class="project-contract-card__label">付款节点</div>

          <ContractPaymentMiniBar
            class="project-contract-card__payment-chart"
            :nodes="contract.nodes"
          />
        </article>
      </section>
    </div>
  </Page>
</template>

<style scoped>
.project-contract-page {
  display: flex;
  flex-direction: column;
  gap: var(--st-layout-section-gap);
  min-height: 100%;
}

.project-contract-stat-grid,
.project-contract-card-grid {
  display: grid;
  gap: var(--st-layout-section-gap);
}

.project-contract-stat-grid {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.project-contract-card-grid {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.project-contract-card {
  min-width: 0;
  overflow: hidden;
  background: hsl(var(--st-color-card-bg));
  border: 1px solid hsl(var(--border));
  border-radius: var(--st-radius-card);
  box-shadow: var(--st-shadow-stat-card);
}

.project-contract-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  min-height: 48px;
  gap: 12px;
  padding: 18px var(--st-module-content-padding);
  border-bottom: 1px solid hsl(var(--border));
}

.project-contract-card__header h2 {
  min-width: 0;
  margin: 0;
  overflow-wrap: anywhere;
  font-size: 16px;
  font-weight: 700;
  line-height: 24px;
  color: hsl(var(--foreground));
}

.project-contract-card__summary,
.project-contract-card__label,
.project-contract-card__payment-chart {
  padding-inline: var(--st-module-content-padding);
}

.project-contract-card__summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding-block: 20px 10px;
}

.project-contract-card__summary span,
.project-contract-card__label {
  color: hsl(var(--muted-foreground));
}

.project-contract-card__summary strong {
  color: hsl(var(--foreground));
}

.project-contract-card__label {
  padding-block: 4px 8px;
  font-weight: 600;
}

.project-contract-card__payment-chart {
  padding-bottom: 16px;
}

@media (max-width: 1200px) {
  .project-contract-card-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 1080px) {
  .project-contract-stat-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 960px) {
  .project-contract-card-grid {
    grid-template-columns: 1fr;
  }
}
</style>
