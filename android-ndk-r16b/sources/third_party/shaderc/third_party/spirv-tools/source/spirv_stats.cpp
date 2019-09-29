// Copyright (c) 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#include "spirv_stats.h"

#include <cassert>

#include <algorithm>
#include <memory>
#include <string>
#include <vector>

#include "binary.h"
#include "diagnostic.h"
#include "enum_string_mapping.h"
#include "extensions.h"
#include "instruction.h"
#include "id_descriptor.h"
#include "opcode.h"
#include "operand.h"
#include "spirv-tools/libspirv.h"
#include "spirv_endian.h"
#include "spirv_validator_options.h"
#include "validate.h"
#include "val/instruction.h"
#include "val/validation_state.h"

using libspirv::IdDescriptorCollection;
using libspirv::Instruction;
using libspirv::SpirvStats;
using libspirv::ValidationState_t;

namespace {

// Helper class for stats aggregation. Receives as in/out parameter.
// Constructs ValidationState and updates it by running validator for each
// instruction.
class StatsAggregator {
 public:
  StatsAggregator(SpirvStats* in_out_stats, const spv_const_context context) {
    stats_ = in_out_stats;
    vstate_.reset(new ValidationState_t(context, &validator_options_));
  }

  // Collects header statistics and sets correct id_bound.
  spv_result_t ProcessHeader(
      spv_endianness_t /* endian */, uint32_t /* magic */,
      uint32_t version, uint32_t generator, uint32_t id_bound,
      uint32_t /* schema */) {
    vstate_->setIdBound(id_bound);
    ++stats_->version_hist[version];
    ++stats_->generator_hist[generator];
    return SPV_SUCCESS;
  }

  // Runs validator to validate the instruction and update vstate_,
  // then procession the instruction to collect stats.
  spv_result_t ProcessInstruction(const spv_parsed_instruction_t* inst) {
    const spv_result_t validation_result =
        spvtools::ValidateInstructionAndUpdateValidationState(vstate_.get(), inst);
    if (validation_result != SPV_SUCCESS)
      return validation_result;

    ProcessOpcode();
    ProcessCapability();
    ProcessExtension();
    ProcessConstant();
    ProcessEnums();
    ProcessLiteralStrings();
    ProcessNonIdWords();
    ProcessIdDescriptors();

    return SPV_SUCCESS;
  }

  // Collects statistics of descriptors generated by IdDescriptorCollection.
  void ProcessIdDescriptors() {
    const Instruction& inst = GetCurrentInstruction();
    const uint32_t new_descriptor =
        id_descriptors_.ProcessInstruction(inst.c_inst());

    if (new_descriptor) {
      std::stringstream ss;
      ss << spvOpcodeString(inst.opcode());
      for (size_t i = 1; i < inst.words().size(); ++i) {
        ss << " " << inst.word(i);
      }
      stats_->id_descriptor_labels.emplace(new_descriptor, ss.str());
    }

    uint32_t index = 0;
    for (const auto& operand : inst.operands()) {
      if (spvIsIdType(operand.type)) {
        const uint32_t descriptor =
            id_descriptors_.GetDescriptor(inst.word(operand.offset));
        if (descriptor) {
          ++stats_->id_descriptor_hist[descriptor];
          ++stats_->operand_slot_id_descriptor_hist[
              std::pair<uint32_t, uint32_t>(inst.opcode(), index)][descriptor];
        }
      }
      ++index;
    }
  }

  // Collects statistics of enum words for operands of specific types.
  void ProcessEnums() {
    const Instruction& inst = GetCurrentInstruction();
    for (const auto& operand : inst.operands()) {
      switch (operand.type) {
        case SPV_OPERAND_TYPE_SOURCE_LANGUAGE:
        case SPV_OPERAND_TYPE_EXECUTION_MODEL:
        case SPV_OPERAND_TYPE_ADDRESSING_MODEL:
        case SPV_OPERAND_TYPE_MEMORY_MODEL:
        case SPV_OPERAND_TYPE_EXECUTION_MODE:
        case SPV_OPERAND_TYPE_STORAGE_CLASS:
        case SPV_OPERAND_TYPE_DIMENSIONALITY:
        case SPV_OPERAND_TYPE_SAMPLER_ADDRESSING_MODE:
        case SPV_OPERAND_TYPE_SAMPLER_FILTER_MODE:
        case SPV_OPERAND_TYPE_SAMPLER_IMAGE_FORMAT:
        case SPV_OPERAND_TYPE_IMAGE_CHANNEL_ORDER:
        case SPV_OPERAND_TYPE_IMAGE_CHANNEL_DATA_TYPE:
        case SPV_OPERAND_TYPE_FP_ROUNDING_MODE:
        case SPV_OPERAND_TYPE_LINKAGE_TYPE:
        case SPV_OPERAND_TYPE_ACCESS_QUALIFIER:
        case SPV_OPERAND_TYPE_FUNCTION_PARAMETER_ATTRIBUTE:
        case SPV_OPERAND_TYPE_DECORATION:
        case SPV_OPERAND_TYPE_BUILT_IN:
        case SPV_OPERAND_TYPE_GROUP_OPERATION:
        case SPV_OPERAND_TYPE_KERNEL_ENQ_FLAGS:
        case SPV_OPERAND_TYPE_KERNEL_PROFILING_INFO:
        case SPV_OPERAND_TYPE_CAPABILITY: {
          ++stats_->enum_hist[operand.type][inst.word(operand.offset)];
          break;
        }
        default:
          break;
      }
    }
  }

  // Collects statistics of literal strings used by opcodes.
  void ProcessLiteralStrings() {
    const Instruction& inst = GetCurrentInstruction();
    for (const auto& operand : inst.operands()) {
      if (operand.type == SPV_OPERAND_TYPE_LITERAL_STRING) {
        const std::string str =
            reinterpret_cast<const char*>(&inst.words()[operand.offset]);
        ++stats_->literal_strings_hist[inst.opcode()][str];
      }
    }
  }

  // Collects statistics of all single word non-id operand slots.
  void ProcessNonIdWords() {
    const Instruction& inst = GetCurrentInstruction();
    uint32_t index = 0;
    for (const auto& operand : inst.operands()) {
      if (operand.num_words == 1 && !spvIsIdType(operand.type)) {
          ++stats_->operand_slot_non_id_words_hist[std::pair<uint32_t, uint32_t>(
              inst.opcode(), index)][inst.word(operand.offset)];
      }
      ++index;
    }
  }

  // Collects OpCapability statistics.
  void ProcessCapability() {
    const Instruction& inst = GetCurrentInstruction();
    if (inst.opcode() != SpvOpCapability) return;
    const uint32_t capability = inst.word(inst.operands()[0].offset);
    ++stats_->capability_hist[capability];
  }

  // Collects OpExtension statistics.
  void ProcessExtension() {
    const Instruction& inst = GetCurrentInstruction();
    if (inst.opcode() != SpvOpExtension) return;
    const std::string extension = libspirv::GetExtensionString(&inst.c_inst());
    ++stats_->extension_hist[extension];
  }

  // Collects OpCode statistics.
  void ProcessOpcode() {
    auto inst_it = vstate_->ordered_instructions().rbegin();
    const SpvOp opcode = inst_it->opcode();
    ++stats_->opcode_hist[opcode];

    const uint32_t opcode_and_num_operands =
        (uint32_t(inst_it->operands().size()) << 16) | uint32_t(opcode);
    ++stats_->opcode_and_num_operands_hist[opcode_and_num_operands];

    ++inst_it;

    if (inst_it != vstate_->ordered_instructions().rend()) {
      const SpvOp prev_opcode = inst_it->opcode();
      ++stats_->opcode_and_num_operands_markov_hist[prev_opcode][
          opcode_and_num_operands];
    }

    auto step_it = stats_->opcode_markov_hist.begin();
    for (; inst_it != vstate_->ordered_instructions().rend() &&
         step_it != stats_->opcode_markov_hist.end(); ++inst_it, ++step_it) {
      auto& hist = (*step_it)[inst_it->opcode()];
      ++hist[opcode];
    }
  }

  // Collects OpConstant statistics.
  void ProcessConstant() {
    const Instruction& inst = GetCurrentInstruction();
    if (inst.opcode() != SpvOpConstant) return;
    const uint32_t type_id = inst.GetOperandAs<uint32_t>(0);
    const auto type_decl_it = vstate_->all_definitions().find(type_id);
    assert(type_decl_it != vstate_->all_definitions().end());
    const Instruction& type_decl_inst = *type_decl_it->second;
    const SpvOp type_op = type_decl_inst.opcode();
    if (type_op == SpvOpTypeInt) {
      const uint32_t bit_width = type_decl_inst.GetOperandAs<uint32_t>(1);
      const uint32_t is_signed = type_decl_inst.GetOperandAs<uint32_t>(2);
      assert(is_signed == 0 || is_signed == 1);
      if (bit_width == 16) {
        if (is_signed)
          ++stats_->s16_constant_hist[inst.GetOperandAs<int16_t>(2)];
        else
          ++stats_->u16_constant_hist[inst.GetOperandAs<uint16_t>(2)];
      } else if (bit_width == 32) {
        if (is_signed)
          ++stats_->s32_constant_hist[inst.GetOperandAs<int32_t>(2)];
        else
          ++stats_->u32_constant_hist[inst.GetOperandAs<uint32_t>(2)];
      } else if (bit_width == 64) {
        if (is_signed)
          ++stats_->s64_constant_hist[inst.GetOperandAs<int64_t>(2)];
        else
          ++stats_->u64_constant_hist[inst.GetOperandAs<uint64_t>(2)];
      } else {
        assert(false && "TypeInt bit width is not 16, 32 or 64");
      }
    } else if (type_op == SpvOpTypeFloat) {
      const uint32_t bit_width = type_decl_inst.GetOperandAs<uint32_t>(1);
      if (bit_width == 32) {
        ++stats_->f32_constant_hist[inst.GetOperandAs<float>(2)];
      } else if (bit_width == 64) {
        ++stats_->f64_constant_hist[inst.GetOperandAs<double>(2)];
      } else {
        assert(bit_width == 16);
      }
    }
  }

  SpirvStats* stats() {
    return stats_;
  }

 private:
  // Returns the current instruction (the one last processed by the validator).
  const Instruction& GetCurrentInstruction() const {
    return vstate_->ordered_instructions().back();
  }

  SpirvStats* stats_;
  spv_validator_options_t validator_options_;
  std::unique_ptr<ValidationState_t> vstate_;
  IdDescriptorCollection id_descriptors_;
};

spv_result_t ProcessHeader(
    void* user_data, spv_endianness_t endian, uint32_t magic,
    uint32_t version, uint32_t generator, uint32_t id_bound,
    uint32_t schema) {
  StatsAggregator* stats_aggregator =
      reinterpret_cast<StatsAggregator*>(user_data);
  return stats_aggregator->ProcessHeader(
      endian, magic, version, generator, id_bound, schema);
}

spv_result_t ProcessInstruction(
    void* user_data, const spv_parsed_instruction_t* inst) {
  StatsAggregator* stats_aggregator =
      reinterpret_cast<StatsAggregator*>(user_data);
  return stats_aggregator->ProcessInstruction(inst);
}

}  // namespace

namespace libspirv {

spv_result_t AggregateStats(
    const spv_context_t& context, const uint32_t* words, const size_t num_words,
    spv_diagnostic* pDiagnostic, SpirvStats* stats) {
  spv_const_binary_t binary = {words, num_words};

  spv_endianness_t endian;
  spv_position_t position = {};
  if (spvBinaryEndianness(&binary, &endian)) {
    return libspirv::DiagnosticStream(position, context.consumer,
                                      SPV_ERROR_INVALID_BINARY)
        << "Invalid SPIR-V magic number.";
  }

  spv_header_t header;
  if (spvBinaryHeaderGet(&binary, endian, &header)) {
    return libspirv::DiagnosticStream(position, context.consumer,
                                      SPV_ERROR_INVALID_BINARY)
        << "Invalid SPIR-V header.";
  }

  StatsAggregator stats_aggregator(stats, &context);

  return spvBinaryParse(&context, &stats_aggregator, words, num_words,
                        ProcessHeader, ProcessInstruction, pDiagnostic);
}

}  // namespace libspirv
