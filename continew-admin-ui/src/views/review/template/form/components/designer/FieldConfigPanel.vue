<template>
  <div class="field-config-panel">
    <div class="panel-header">
      <h3>字段配置</h3>
    </div>

    <div class="panel-body">
      <!-- 未选中字段 -->
      <a-empty v-if="!selectedField" description="请选择一个字段进行配置">
        <template #image>
          <icon-settings style="font-size: 48px; color: var(--color-text-4);" />
        </template>
      </a-empty>

      <!-- 已选中字段 -->
      <a-form v-else :model="localField" layout="vertical" size="small">
        <!-- 基本配置 -->
        <div class="config-section">
          <div class="section-title">基本配置</div>

          <a-form-item label="字段名称" required>
            <a-input
              v-model="localField.fieldName"
              placeholder="请输入字段名称"
              @change="handleUpdate"
            />
          </a-form-item>

          <a-form-item label="字段编码" required>
            <a-input
              v-model="localField.fieldCode"
              placeholder="请输入字段编码"
              @change="handleUpdate"
            />
          </a-form-item>

          <a-form-item label="字段类型">
            <a-select v-model="localField.fieldType" disabled>
              <a-option
                v-for="option in fieldTypeOptions"
                :key="option.value"
                :value="option.value"
                :label="option.label"
              />
            </a-select>
          </a-form-item>

          <a-form-item label="栅格占位">
            <a-slider
              v-model="localField.span"
              :min="1"
              :max="24"
              :marks="{ 1: '1', 6: '6', 12: '12', 18: '18', 24: '24' }"
              show-tooltip
              @change="handleUpdate"
            />
          </a-form-item>

          <a-form-item v-if="localField.fieldType !== 'FILE_TEMPLATE'" label="是否必填">
            <a-switch
              v-model="localField.isRequired"
              @change="handleUpdate"
            />
          </a-form-item>

          <a-form-item v-if="localField.fieldType !== 'FILE_TEMPLATE'" label="是否显示">
            <a-switch
              v-model="localField.isVisible"
              @change="handleUpdate"
            />
          </a-form-item>

          <a-form-item v-if="localField.fieldType !== 'FILE_TEMPLATE'" label="是否只读">
            <a-switch
              v-model="localField.isReadonly"
              @change="handleUpdate"
            />
          </a-form-item>

          <a-form-item label="排序">
            <a-input-number
              v-model="localField.sort"
              :min="1"
              :style="{ width: '100%' }"
              @change="handleUpdate"
            />
          </a-form-item>
        </div>

        <!-- 字段配置 -->
        <div class="config-section">
          <div class="section-title">字段配置</div>

          <!-- 单行文本 -->
          <template v-if="localField.fieldType === 'TEXT'">
            <a-form-item label="默认值">
              <a-input
                v-model="localField.fieldConfig.defaultValue"
                placeholder="请输入默认值"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="占位提示">
              <a-input
                v-model="localField.fieldConfig.placeholder"
                placeholder="请输入占位提示"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="最大长度">
              <a-input-number
                v-model="localField.fieldConfig.maxLength"
                :min="1"
                :max="5000"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
          </template>

          <!-- 多行文本 -->
          <template v-else-if="localField.fieldType === 'TEXTAREA'">
            <a-form-item label="默认值">
              <a-textarea
                v-model="localField.fieldConfig.defaultValue"
                placeholder="请输入默认值"
                :auto-size="{ minRows: 2, maxRows: 4 }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="占位提示">
              <a-input
                v-model="localField.fieldConfig.placeholder"
                placeholder="请输入占位提示"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="行数">
              <a-input-number
                v-model="localField.fieldConfig.rows"
                :min="2"
                :max="20"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="最大长度">
              <a-input-number
                v-model="localField.fieldConfig.maxLength"
                :min="1"
                :max="10000"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="显示字数统计">
              <a-switch
                v-model="localField.fieldConfig.showWordCount"
                @change="handleUpdate"
              />
            </a-form-item>
          </template>

          <!-- 数字 -->
          <template v-else-if="localField.fieldType === 'NUMBER'">
            <a-form-item label="默认值">
              <a-input-number
                v-model="localField.fieldConfig.defaultValue"
                placeholder="请输入默认值"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="占位提示">
              <a-input
                v-model="localField.fieldConfig.placeholder"
                placeholder="请输入占位提示"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="最小值">
              <a-input-number
                v-model="localField.fieldConfig.min"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="最大值">
              <a-input-number
                v-model="localField.fieldConfig.max"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="精度">
              <a-input-number
                v-model="localField.fieldConfig.precision"
                :min="0"
                :max="10"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
          </template>

          <!-- 日期 -->
          <template v-else-if="localField.fieldType === 'DATE'">
            <a-form-item label="默认值">
              <a-date-picker
                v-model="localField.fieldConfig.defaultValue"
                :format="localField.fieldConfig.format || 'YYYY-MM-DD'"
                :show-time="isDateWithTime(localField.fieldConfig.format)"
                :mode="getDatePickerMode(localField.fieldConfig.format)"
                placeholder="请选择默认日期"
                allow-clear
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="占位提示">
              <a-input
                v-model="localField.fieldConfig.placeholder"
                placeholder="请输入占位提示"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="日期格式">
              <a-select
                v-model="localField.fieldConfig.format"
                @change="handleUpdate"
              >
                <a-option value="YYYY-MM-DD">年-月-日</a-option>
                <a-option value="YYYY-MM-DD HH:mm:ss">年-月-日 时:分:秒</a-option>
                <a-option value="YYYY-MM">年-月</a-option>
              </a-select>
            </a-form-item>
          </template>

          <!-- 下拉选择/单选/多选 -->
          <template v-else-if="['SELECT', 'RADIO', 'CHECKBOX'].includes(localField.fieldType)">
            <a-form-item v-if="localField.fieldType === 'SELECT'" label="占位提示">
              <a-input
                v-model="localField.fieldConfig.placeholder"
                placeholder="请输入占位提示"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="默认值">
              <a-select
                v-if="localField.fieldType === 'CHECKBOX'"
                v-model="localField.fieldConfig.defaultValue"
                :options="localField.fieldConfig.options || []"
                placeholder="请选择默认值"
                multiple
                allow-clear
                @change="handleUpdate"
              />
              <a-select
                v-else
                v-model="localField.fieldConfig.defaultValue"
                :options="localField.fieldConfig.options || []"
                placeholder="请选择默认值"
                allow-clear
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="选项配置">
              <div class="options-list">
                <template v-if="localField.fieldConfig.options && localField.fieldConfig.options.length > 0">
                  <div
                    v-for="(option, index) in localField.fieldConfig.options"
                    :key="index"
                    class="option-item"
                  >
                    <div class="option-title">选项{{ index + 1 }}</div>
                    <div class="option-fields">
                      <a-input
                        v-model="option.label"
                        placeholder="标签"
                        size="small"
                        @input="handleUpdate"
                      />
                      <a-input
                        v-model="option.value"
                        placeholder="值"
                        size="small"
                        @input="handleUpdate"
                      />
                      <a-button
                        size="small"
                        type="text"
                        status="danger"
                        @click="removeOption(index)"
                      >
                        <icon-delete />
                      </a-button>
                    </div>
                  </div>
                </template>
                <a-empty v-else description="暂无选项" :style="{ marginBottom: '12px' }" />
              </div>
            </a-form-item>

            <a-form-item>
              <a-button size="small" type="dashed" long @click="addOption">
                <icon-plus /> 添加选项
              </a-button>
            </a-form-item>
            <a-form-item v-if="localField.fieldType === 'SELECT'" label="允许清除">
              <a-switch
                v-model="localField.fieldConfig.allowClear"
                @change="handleUpdate"
              />
            </a-form-item>
          </template>

          <!-- 评分 -->
          <template v-else-if="localField.fieldType === 'SCORE'">
            <a-form-item label="默认值">
              <a-input-number
                v-model="localField.fieldConfig.defaultValue"
                :min="0"
                :max="localField.fieldConfig.count || 5"
                :step="localField.fieldConfig.allowHalf ? 0.5 : 1"
                placeholder="请输入默认评分"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="星星数量">
              <a-input-number
                v-model="localField.fieldConfig.count"
                :min="1"
                :max="10"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="允许半星">
              <a-switch
                v-model="localField.fieldConfig.allowHalf"
                @change="handleUpdate"
              />
            </a-form-item>
          </template>

          <!-- 文件上传 -->
          <template v-else-if="localField.fieldType === 'FILE'">
            <a-form-item label="最大文件数">
              <a-input-number
                v-model="localField.fieldConfig.maxCount"
                :min="1"
                :max="50"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="最大文件大小(MB)">
              <a-input-number
                v-model="localField.fieldConfig.maxSize"
                :min="1"
                :max="200"
                :style="{ width: '100%' }"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="提示信息">
              <a-textarea
                v-model="localField.fieldConfig.tips"
                placeholder="请输入提示信息"
                :auto-size="{ minRows: 2, maxRows: 4 }"
                @change="handleUpdate"
              />
            </a-form-item>
          </template>

          <!-- 文件模板 -->
          <template v-else-if="localField.fieldType === 'FILE_TEMPLATE'">
            <a-form-item label="模板文件">
              <a-upload
                :file-list="localField.fieldConfig.templateFiles || []"
                :limit="10"
                action=""
                :custom-request="handleTemplateFileUpload"
                @change="handleTemplateFileChange"
              >
                <template #upload-button>
                  <a-button type="outline">
                    <icon-upload />
                    上传模板文件
                  </a-button>
                </template>
              </a-upload>
            </a-form-item>
            <a-form-item label="允许下载">
              <a-switch
                v-model="localField.fieldConfig.allowDownload"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="允许预览">
              <a-switch
                v-model="localField.fieldConfig.allowPreview"
                @change="handleUpdate"
              />
            </a-form-item>
            <a-form-item label="提示信息">
              <a-textarea
                v-model="localField.fieldConfig.tips"
                placeholder="请输入提示信息"
                :auto-size="{ minRows: 2, maxRows: 4 }"
                @change="handleUpdate"
              />
            </a-form-item>
          </template>

          <!-- 动态表格 -->
          <template v-else-if="localField.fieldType === 'TABLE'">
            <a-form-item label="列配置">
              <div class="table-columns-list">
                <template v-if="localField.fieldConfig.columns && localField.fieldConfig.columns.length > 0">
                  <div
                    v-for="(column, index) in localField.fieldConfig.columns"
                    :key="index"
                    class="column-item"
                  >
                    <div class="column-title">列{{ index + 1 }}</div>
                    <div class="column-fields">
                      <div class="column-field">
                        <label>列名</label>
                        <a-input
                          v-model="column.name"
                          placeholder="列名"
                          size="small"
                          @input="handleUpdate"
                        />
                      </div>
                      <div class="column-field">
                        <label>编码</label>
                        <a-input
                          v-model="column.code"
                          placeholder="编码"
                          size="small"
                          @input="handleUpdate"
                        />
                      </div>
                      <div class="column-field">
                        <label>类型</label>
                        <a-select
                          v-model="column.type"
                          size="small"
                          @change="handleUpdate"
                        >
                          <a-option value="TEXT">文本</a-option>
                          <a-option value="NUMBER">数字</a-option>
                          <a-option value="SELECT">下拉框</a-option>
                        </a-select>
                      </div>
                      <a-button
                        size="small"
                        type="text"
                        status="danger"
                        @click="removeTableColumn(index)"
                      >
                        <icon-delete />
                      </a-button>
                    </div>
                    <!-- 下拉框选项配置 -->
                    <div v-if="column.type === 'SELECT'" class="column-options">
                      <div class="options-title">下拉选项</div>
                      <div class="options-inputs">
                        <template v-if="column.options && column.options.length > 0">
                          <div
                            v-for="(opt, optIndex) in column.options"
                            :key="optIndex"
                            class="option-input-row"
                          >
                            <a-input
                              v-model="opt.label"
                              placeholder="标签"
                              size="mini"
                              @input="handleUpdate"
                            />
                            <a-input
                              v-model="opt.value"
                              placeholder="值"
                              size="mini"
                              @input="handleUpdate"
                            />
                            <a-button
                              size="mini"
                              type="text"
                              status="danger"
                              @click="removeColumnOption(index, optIndex)"
                            >
                              <icon-delete />
                            </a-button>
                          </div>
                        </template>
                      </div>
                      <a-button size="mini" type="text" @click="addColumnOption(index)">
                        <icon-plus /> 添加选项
                      </a-button>
                    </div>
                  </div>
                </template>
                <a-empty v-else description="暂无列配置" :style="{ marginBottom: '12px' }" />
              </div>
            </a-form-item>

            <a-form-item>
              <a-button size="small" type="dashed" long @click="addTableColumn">
                <icon-plus /> 添加列
              </a-button>
            </a-form-item>
          </template>

          <!-- 评分表 -->
          <template v-else-if="localField.fieldType === 'SCORE_TABLE'">
            <!-- 计分模式 -->
            <a-form-item label="计分模式">
              <div class="score-mode-panel">
                <a-radio-group v-model="localField.fieldConfig.scoreMode" @change="handleUpdate">
                  <a-radio value="WEIGHTED">加权求和</a-radio>
                  <a-radio value="SIMPLE">简单相加</a-radio>
                </a-radio-group>
              </div>
            </a-form-item>

            <!-- 及格标准配置 -->
            <a-form-item label="及格标准配置">
              <div class="pass-config-panel">
                <div class="pass-config-row">
                  <label>标准类型</label>
                  <a-radio-group
                    v-model="localField.fieldConfig.passConfig.passType"
                    @change="handleUpdate"
                  >
                    <a-radio value="RATIO">按比例</a-radio>
                    <a-radio value="FIXED">固定分数</a-radio>
                  </a-radio-group>
                </div>

                <div class="pass-config-row">
                  <label>及格值</label>
                  <a-input-number
                    v-model="localField.fieldConfig.passConfig.passValue"
                    :min="0"
                    :max="localField.fieldConfig.passConfig.passType === 'RATIO' ? 100 : computedTotalScore"
                    :precision="0"
                    :style="{ width: '120px' }"
                    @change="handleUpdate"
                  >
                    <template #suffix>
                      {{ localField.fieldConfig.passConfig.passType === 'RATIO' ? '%' : '分' }}
                    </template>
                  </a-input-number>
                </div>

                <div class="pass-config-row">
                  <label>良好值（可选）</label>
                  <a-input-number
                    v-model="localField.fieldConfig.passConfig.goodValue"
                    :min="0"
                    :max="localField.fieldConfig.passConfig.passType === 'RATIO' ? 100 : computedTotalScore"
                    :precision="0"
                    :style="{ width: '120px' }"
                    @change="handleUpdate"
                  >
                    <template #suffix>
                      {{ localField.fieldConfig.passConfig.passType === 'RATIO' ? '%' : '分' }}
                    </template>
                  </a-input-number>
                </div>

                <div class="pass-config-row">
                  <label>优秀值（可选）</label>
                  <a-input-number
                    v-model="localField.fieldConfig.passConfig.excellentValue"
                    :min="0"
                    :max="localField.fieldConfig.passConfig.passType === 'RATIO' ? 100 : computedTotalScore"
                    :precision="0"
                    :style="{ width: '120px' }"
                    @change="handleUpdate"
                  >
                    <template #suffix>
                      {{ localField.fieldConfig.passConfig.passType === 'RATIO' ? '%' : '分' }}
                    </template>
                  </a-input-number>
                </div>
              </div>
            </a-form-item>

            <!-- 评分项列表 -->
            <a-form-item label="评分项列表">
              <div class="score-items-list">
                <template v-if="localField.fieldConfig.scoreItems && localField.fieldConfig.scoreItems.length > 0">
                  <div
                    v-for="(item, index) in localField.fieldConfig.scoreItems"
                    :key="item.id"
                    class="score-item"
                  >
                    <div class="score-item-header">
                      <span class="score-item-index">{{ index + 1 }}.</span>
                      <a-input
                        v-model="item.itemName"
                        placeholder="评分项名称"
                        size="small"
                        :style="{ width: '140px' }"
                        @input="handleUpdate"
                      />
                      <div class="score-item-actions">
                        <a-button
                          size="mini"
                          type="text"
                          :disabled="index === 0"
                          @click="moveScoreItemUp(index)"
                        >
                          <icon-up />
                        </a-button>
                        <a-button
                          size="mini"
                          type="text"
                          :disabled="index === localField.fieldConfig.scoreItems.length - 1"
                          @click="moveScoreItemDown(index)"
                        >
                          <icon-down />
                        </a-button>
                        <a-button
                          size="mini"
                          type="text"
                          status="danger"
                          @click="removeScoreItem(index)"
                        >
                          <icon-delete />
                        </a-button>
                      </div>
                    </div>

                    <!-- 第一行：编码、满分、权重(仅加权模式) -->
                    <div class="score-item-row">
                      <div class="score-field">
                        <label>编码</label>
                        <a-input
                          v-model="item.itemCode"
                          placeholder="item1"
                          size="small"
                          @input="handleUpdate"
                        />
                      </div>
                      <div class="score-field">
                        <label>满分</label>
                        <a-input-number
                          v-model="item.maxScore"
                          :min="0"
                          :max="1000"
                          :precision="0"
                          size="small"
                          @change="handleUpdate"
                        />
                      </div>
                      <div v-if="localField.fieldConfig.scoreMode === 'WEIGHTED'" class="score-field">
                        <label>权重</label>
                        <a-input-number
                          v-model="item.weight"
                          :min="0"
                          :max="1"
                          :precision="2"
                          :step="0.1"
                          size="small"
                          @change="handleUpdate"
                        />
                      </div>
                    </div>

                    <!-- 第二行：必填、允许小数、小数位数 -->
                    <div class="score-item-row score-options-row">
                      <a-space size="medium">
                        <a-checkbox
                          v-model="item.required"
                          @change="handleUpdate"
                        >
                          必填
                        </a-checkbox>
                        <a-checkbox
                          v-model="item.allowDecimal"
                          @change="handleUpdate"
                        >
                          允许小数
                        </a-checkbox>
                        <div v-if="item.allowDecimal" style="display: inline-flex; align-items: center; gap: 6px;">
                          <span style="font-size: 12px; color: var(--color-text-3);">小数位数:</span>
                          <a-input-number
                            v-model="item.decimalPlaces"
                            :min="1"
                            :max="2"
                            :precision="0"
                            size="small"
                            :style="{ width: '80px' }"
                            @change="handleUpdate"
                          />
                        </div>
                      </a-space>
                    </div>

                    <!-- 第三行：评分说明 -->
                    <div class="score-item-row">
                      <div class="score-field-full">
                        <label>评分说明</label>
                        <a-textarea
                          v-model="item.description"
                          placeholder="请输入评分项说明"
                          :auto-size="{ minRows: 2, maxRows: 3 }"
                          size="small"
                          @input="handleUpdate"
                        />
                      </div>
                    </div>
                  </div>
                </template>
                <a-empty v-else description="暂无评分项" :style="{ marginBottom: '12px' }" />
              </div>
            </a-form-item>

            <a-form-item>
              <a-button size="small" type="dashed" long @click="addScoreItem">
                <icon-plus /> 添加评分项
              </a-button>
            </a-form-item>

            <!-- 汇总信息 -->
            <a-form-item label="汇总信息">
              <a-space direction="vertical" :style="{ width: '100%' }">
                <a-alert type="info">
                  总分: {{ computedTotalScore }} 分
                </a-alert>
                <a-alert
                  v-if="localField.fieldConfig.scoreMode === 'WEIGHTED'"
                  :type="Math.abs(computedTotalWeight - 1) < 0.01 ? 'success' : 'warning'"
                >
                  权重总和: {{ computedTotalWeight }}
                  {{ Math.abs(computedTotalWeight - 1) < 0.01 ? '✓' : '(建议总和为1.0)' }}
                </a-alert>
              </a-space>
            </a-form-item>

            <!-- 显示选项 -->
            <a-form-item label="显示选项">
              <a-space direction="vertical">
                <a-checkbox
                  v-if="localField.fieldConfig.scoreMode === 'WEIGHTED'"
                  v-model="localField.fieldConfig.displayConfig.showWeight"
                  @change="handleUpdate"
                >
                  显示权重列
                </a-checkbox>
                <a-checkbox
                  v-model="localField.fieldConfig.displayConfig.showDescription"
                  @change="handleUpdate"
                >
                  显示评分说明
                </a-checkbox>
                <a-checkbox
                  v-model="localField.fieldConfig.displayConfig.showProgress"
                  @change="handleUpdate"
                >
                  显示进度条
                </a-checkbox>
                <a-checkbox
                  v-model="localField.fieldConfig.displayConfig.showSummary"
                  @change="handleUpdate"
                >
                  显示汇总信息
                </a-checkbox>
              </a-space>
            </a-form-item>
          </template>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { Message } from '@arco-design/web-vue'
import type { RequestOption } from '@arco-design/web-vue'
import type { FormFieldReq } from '@/apis/review'
import { uploadFile } from '@/apis/system/file'

defineOptions({ name: 'FieldConfigPanel' })

interface Props {
  selectedField?: FormFieldReq | null
  fieldIndex?: number | null
}

const props = withDefaults(defineProps<Props>(), {
  selectedField: null,
  fieldIndex: null,
})

const emit = defineEmits<{
  (e: 'update', field: FormFieldReq): void
}>()

// 字段类型选项
const fieldTypeOptions = [
  { label: '单行文本', value: 'TEXT' },
  { label: '多行文本', value: 'TEXTAREA' },
  { label: '数字', value: 'NUMBER' },
  { label: '日期', value: 'DATE' },
  { label: '下拉选择', value: 'SELECT' },
  { label: '单选', value: 'RADIO' },
  { label: '多选', value: 'CHECKBOX' },
  { label: '评分', value: 'SCORE' },
  { label: '文件上传', value: 'FILE' },
  { label: '文件模板', value: 'FILE_TEMPLATE' },
  { label: '动态表格', value: 'TABLE' },
  { label: '评分表', value: 'SCORE_TABLE' },
]

// 本地字段数据
const localField = ref<FormFieldReq | null>(null)

// 判断日期是否需要显示时间选择器
const isDateWithTime = (format?: string) => {
  if (!format)
    return false
  // 如果格式包含时分秒（HH、mm、ss），则显示时间选择器
  return /HH|mm|ss|hh|H|h|m|s/.test(format)
}

// 获取日期选择器的模式
const getDatePickerMode = (format?: string) => {
  if (!format)
    return 'date'
  // 如果格式只包含年月（YYYY-MM），使用月份模式
  if (/^YYYY-MM$/.test(format)) {
    return 'month'
  }
  // 如果格式只包含年（YYYY），使用年份模式
  if (/^YYYY$/.test(format)) {
    return 'year'
  }
  return 'date'
}

// 监听选中字段变化
watch(
  () => props.selectedField,
  (newField) => {
    if (newField) {
      // 深拷贝字段数据
      localField.value = JSON.parse(JSON.stringify(newField))
      // 确保 fieldConfig 存在
      if (!localField.value.fieldConfig) {
        localField.value.fieldConfig = {}
      }
      // 确保 isVisible 和 isReadonly 字段存在
      if (localField.value.isVisible === undefined) {
        localField.value.isVisible = true
      }
      if (localField.value.isReadonly === undefined) {
        localField.value.isReadonly = false
      }
      // 确保选择类字段的options数组存在且格式正确
      if (['SELECT', 'RADIO', 'CHECKBOX'].includes(localField.value.fieldType)) {
        if (!localField.value.fieldConfig.options || !Array.isArray(localField.value.fieldConfig.options)) {
          localField.value.fieldConfig.options = []
        }
        // 确保每个选项都有 label 和 value 属性，过滤掉无效选项
        localField.value.fieldConfig.options = localField.value.fieldConfig.options
          .map((opt: any) => {
            if (typeof opt === 'object' && opt !== null) {
              return {
                label: opt.label || '',
                value: opt.value || '',
              }
            }
            return null
          })
          .filter((opt: any) => opt !== null)
      }
      // 确保表格类字段的columns数组存在
      if (localField.value.fieldType === 'TABLE') {
        if (!localField.value.fieldConfig.columns || !Array.isArray(localField.value.fieldConfig.columns)) {
          localField.value.fieldConfig.columns = []
        }
      }
      // 确保评分表字段的配置存在
      if (localField.value.fieldType === 'SCORE_TABLE') {
        if (!localField.value.fieldConfig.scoreMode) {
          localField.value.fieldConfig.scoreMode = 'WEIGHTED'
        }
        if (!localField.value.fieldConfig.passConfig) {
          localField.value.fieldConfig.passConfig = {
            passType: 'RATIO',
            passValue: 60,
          }
        }
        if (!localField.value.fieldConfig.scoreItems || !Array.isArray(localField.value.fieldConfig.scoreItems)) {
          localField.value.fieldConfig.scoreItems = []
        }
        if (!localField.value.fieldConfig.displayConfig) {
          localField.value.fieldConfig.displayConfig = {
            showWeight: true,
            showDescription: true,
            showProgress: true,
            showSummary: true,
            summaryPosition: 'bottom',
          }
        }
      }
    }
    else {
      localField.value = null
    }
  },
  { immediate: true, deep: true },
)

// 更新字段配置
const handleUpdate = () => {
  if (localField.value) {
    emit('update', localField.value)
  }
}

// 处理模板文件上传请求
const handleTemplateFileUpload = (options: RequestOption) => {
  ;(async function requestWrap() {
    const { onProgress, onError, onSuccess, fileItem, name = 'file' } = options
    onProgress(20)
    const formData = new FormData()
    formData.append('parentPath', '/template/')
    formData.append(name as string, fileItem.file as Blob)
    try {
      const res = await uploadFile(formData)
      Message.success('上传成功')
      onSuccess(res)
    }
    catch (error) {
      onError(error)
    }
  })()
  return {
    abort() {
      Message.error('上传已取消')
    },
  }
}

// 处理模板文件上传变化
const handleTemplateFileChange = (fileList: any[], currentFile: any) => {
  if (!localField.value)
    return

  // 保存文件列表
  localField.value.fieldConfig.templateFiles = fileList.map((file: any) => {
    const fileData = file.response || {}
    // 从响应数据中提取 URL（支持多种格式）
    const extractedUrl = fileData.data?.url || fileData.url || fileData.path || file.url || ''

    return {
      uid: file.uid,
      name: file.name || '模板文件',
      status: file.status || 'done',
      url: extractedUrl,
      response: fileData,
    }
  })

  handleUpdate()
}

// 添加选项
const addOption = () => {
  if (!localField.value)
    return

  if (!localField.value.fieldConfig.options) {
    localField.value.fieldConfig.options = []
  }

  localField.value.fieldConfig.options.push({
    label: `选项${localField.value.fieldConfig.options.length + 1}`,
    value: `option${localField.value.fieldConfig.options.length + 1}`,
  })

  handleUpdate()
}

// 删除选项
const removeOption = (index: number) => {
  if (!localField.value || !localField.value.fieldConfig.options)
    return

  localField.value.fieldConfig.options.splice(index, 1)
  handleUpdate()
}

// 添加表格列
const addTableColumn = () => {
  if (!localField.value)
    return

  if (!localField.value.fieldConfig.columns) {
    localField.value.fieldConfig.columns = []
  }

  localField.value.fieldConfig.columns.push({
    name: `列${localField.value.fieldConfig.columns.length + 1}`,
    code: `col${localField.value.fieldConfig.columns.length + 1}`,
    type: 'TEXT',
    width: 150,
    required: false,
  })

  handleUpdate()
}

// 删除表格列
const removeTableColumn = (index: number) => {
  if (!localField.value || !localField.value.fieldConfig.columns)
    return

  localField.value.fieldConfig.columns.splice(index, 1)
  handleUpdate()
}

// 添加列选项
const addColumnOption = (columnIndex: number) => {
  if (!localField.value || !localField.value.fieldConfig.columns)
    return

  const column = localField.value.fieldConfig.columns[columnIndex]
  if (!column.options) {
    column.options = []
  }

  column.options.push({
    label: `选项${column.options.length + 1}`,
    value: `option${column.options.length + 1}`,
  })

  handleUpdate()
}

// 删除列选项
const removeColumnOption = (columnIndex: number, optionIndex: number) => {
  if (!localField.value || !localField.value.fieldConfig.columns)
    return

  const column = localField.value.fieldConfig.columns[columnIndex]
  if (!column.options)
    return

  column.options.splice(optionIndex, 1)
  handleUpdate()
}

// 添加评分项
const addScoreItem = () => {
  if (!localField.value)
    return

  if (!localField.value.fieldConfig.scoreItems) {
    localField.value.fieldConfig.scoreItems = []
  }

  const newItem = {
    id: `score_${Date.now()}_${Math.random().toString(36).slice(2, 11)}`,
    itemName: `评分项${localField.value.fieldConfig.scoreItems.length + 1}`,
    itemCode: `item${localField.value.fieldConfig.scoreItems.length + 1}`,
    maxScore: 10,
    weight: 0.1,
    description: '',
    required: true,
    allowDecimal: true,
    decimalPlaces: 1,
  }

  localField.value.fieldConfig.scoreItems.push(newItem)
  handleUpdate()
}

// 删除评分项
const removeScoreItem = (index: number) => {
  if (!localField.value || !localField.value.fieldConfig.scoreItems)
    return

  localField.value.fieldConfig.scoreItems.splice(index, 1)
  handleUpdate()
}

// 上移评分项
const moveScoreItemUp = (index: number) => {
  if (!localField.value || !localField.value.fieldConfig.scoreItems || index === 0)
    return

  const items = localField.value.fieldConfig.scoreItems
  ;[items[index - 1], items[index]] = [items[index], items[index - 1]]
  handleUpdate()
}

// 下移评分项
const moveScoreItemDown = (index: number) => {
  if (!localField.value || !localField.value.fieldConfig.scoreItems)
    return

  const items = localField.value.fieldConfig.scoreItems
  if (index === items.length - 1)
    return

  ;[items[index], items[index + 1]] = [items[index + 1], items[index]]
  handleUpdate()
}

// 计算总分
const computedTotalScore = computed(() => {
  if (!localField.value || !localField.value.fieldConfig.scoreItems)
    return 0
  return localField.value.fieldConfig.scoreItems.reduce((sum: number, item: any) => sum + (item.maxScore || 0), 0)
})

// 计算权重总和
const computedTotalWeight = computed(() => {
  if (!localField.value || !localField.value.fieldConfig.scoreItems)
    return 0
  const total = localField.value.fieldConfig.scoreItems.reduce((sum: number, item: any) => sum + (item.weight || 0), 0)
  return Math.round(total * 100) / 100
})
</script>

<style lang="scss" scoped>
.field-config-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 16px;

  .panel-header {
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--color-border-2);

    h3 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      color: var(--color-text-1);
    }
  }

  .panel-body {
    flex: 1;
    min-height: 0;
    overflow-y: auto;

    .config-section {
      margin-bottom: 20px;

      .section-title {
        margin-bottom: 12px;
        padding-left: 8px;
        font-size: 14px;
        font-weight: 600;
        color: var(--color-text-2);
        border-left: 3px solid rgb(var(--primary-6));
      }

      .options-list {
        display: flex;
        flex-direction: column;
        width: 100%;

        .option-item {
          margin-bottom: 16px;
          padding: 12px;
          background: var(--color-bg-1);
          border: 1px solid var(--color-border-2);
          border-radius: 4px;

          .option-title {
            font-size: 13px;
            font-weight: 600;
            color: var(--color-text-2);
            margin-bottom: 8px;
          }

          .option-fields {
            display: flex;
            align-items: center;
            gap: 8px;

            :deep(.arco-input) {
              flex: 1;
            }

            :deep(.arco-btn) {
              flex-shrink: 0;
            }
          }
        }
      }

      .table-columns-list {
        display: flex;
        flex-direction: column;
        width: 100%;

        .column-item {
          margin-bottom: 16px;
          padding: 12px;
          background: var(--color-bg-1);
          border: 1px solid var(--color-border-2);
          border-radius: 4px;

          .column-title {
            font-size: 13px;
            font-weight: 600;
            color: var(--color-text-2);
            margin-bottom: 8px;
          }

          .column-fields {
            display: flex;
            align-items: flex-end;
            gap: 8px;

            .column-field {
              flex: 1;
              display: flex;
              flex-direction: column;
              gap: 4px;

              label {
                font-size: 12px;
                color: var(--color-text-3);
              }
            }

            :deep(.arco-btn) {
              flex-shrink: 0;
            }
          }

          .column-options {
            margin-top: 12px;
            padding-top: 12px;
            border-top: 1px dashed var(--color-border-2);

            .options-title {
              font-size: 12px;
              color: var(--color-text-3);
              margin-bottom: 8px;
            }

            .options-inputs {
              margin-bottom: 8px;

              .option-input-row {
                display: flex;
                align-items: center;
                gap: 6px;
                margin-bottom: 6px;

                :deep(.arco-input) {
                  flex: 1;
                }

                :deep(.arco-btn) {
                  flex-shrink: 0;
                }
              }
            }
          }
        }
      }

      // 评分表配置样式
      .score-mode-panel {
        padding: 10px 12px;
        background-color: #f7f8fa;
        border-radius: 6px;
        border: 2px solid #d9d9d9;
      }

      .pass-config-panel {
        padding: 12px;
        background-color: #f7f8fa;
        border-radius: 6px;
        border: 2px solid #d9d9d9;

        .pass-config-row {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 10px;

          &:last-child {
            margin-bottom: 0;
          }

          label {
            min-width: 90px;
            font-size: 12px;
            color: var(--color-text-2);
          }

          :deep(.arco-radio-group) {
            flex: 1;
          }

          :deep(.arco-input-number) {
            width: 140px;
          }
        }
      }

      .score-items-list {
        max-height: 450px;
        overflow-y: auto;
        padding: 10px;
        background-color: #ffffff;
        border-radius: 6px;
        border: 2px solid #d9d9d9 !important;

          &::-webkit-scrollbar {
            width: 4px;
          }

          &::-webkit-scrollbar-thumb {
            background-color: var(--color-border-3);
            border-radius: 2px;

            &:hover {
              background-color: var(--color-border-4);
            }
          }

          .score-item {
            padding: 12px;
            background-color: #fafafa;
            border: 2px solid #c9cdd4;
            border-radius: 6px;
            margin-bottom: 10px;
            transition: all 0.2s;

            &:hover {
              border-color: rgb(var(--primary-6));
              box-shadow: 0 2px 8px rgba(var(--primary-6), 0.15);
            }

            &:last-child {
              margin-bottom: 0;
            }

            .score-item-header {
              display: flex;
              align-items: center;
              gap: 8px;
              margin-bottom: 10px;
              padding-bottom: 8px;
              border-bottom: 1px solid var(--color-border-3);

              .score-item-index {
                font-size: 12px;
                font-weight: 600;
                color: var(--color-text-3);
                min-width: 20px;
              }

              :deep(.arco-input) {
                font-size: 13px;
              }

              .score-item-actions {
                margin-left: auto;
                display: flex;
                gap: 0;
              }
            }

            .score-item-row {
              display: flex;
              align-items: flex-start;
              gap: 8px;
              margin-bottom: 8px;

              &:last-child {
                margin-bottom: 0;
              }

              &.score-options-row {
                padding: 8px 0;
                border-top: 1px dashed var(--color-border-2);
                border-bottom: 1px dashed var(--color-border-2);
                margin: 10px 0;
              }

              .score-field {
                flex: 1;
                display: flex;
                flex-direction: column;
                gap: 4px;

                label {
                  font-size: 12px;
                  color: var(--color-text-3);
                }

                :deep(.arco-input-number),
                :deep(.arco-input),
                :deep(.arco-textarea) {
                  width: 100%;
                }
              }

              .score-field-inline {
                display: flex;
                align-items: center;
                padding-top: 20px;
              }

              .score-field-full {
                flex: 1;
                display: flex;
                flex-direction: column;
                gap: 4px;

                label {
                  font-size: 12px;
                  color: var(--color-text-3);
                }

                :deep(.arco-textarea) {
                  width: 100%;
                }
              }
            }
          }
        }
    }
  }
}
</style>
