package io.opensaber.registry.interceptor.request.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.opensaber.registry.middleware.transform.Data;
import io.opensaber.registry.middleware.transform.ErrorCode;
import io.opensaber.registry.middleware.transform.ITransformer;
import io.opensaber.registry.middleware.transform.TransformationException;
import io.opensaber.registry.middleware.util.Constants.JsonldConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class JsonToLdRequestTransformer implements ITransformer<Object> {

	private static Logger logger = LoggerFactory.getLogger(JsonToLdRequestTransformer.class);
	private String context;
	private final static String REQUEST = "request";
	private List<String> nodeTypes = new ArrayList<>();
	private String sufix = "";
	private static final String SEPERATOR = ":";

	public JsonToLdRequestTransformer(String context) {
		this.context = context;
	}

	@Override
	public Data<Object> transform(Data<Object> data) throws TransformationException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode input = (ObjectNode) mapper.readTree(data.getData().toString());
			ObjectNode fieldObjects = (ObjectNode) mapper.readTree(context);
			setNodeTypeToAppend(fieldObjects);
			ObjectNode requestnode = (ObjectNode) input.path(REQUEST);

			String type = getTypeFromNode(requestnode);
			setSufix(type);
			appendSuffix(requestnode, sufix);
			logger.info("Appending prefix to requestNode " + requestnode);

			requestnode = (ObjectNode) requestnode.path(type);
			requestnode.setAll(fieldObjects);
			input.set(REQUEST, requestnode);
			logger.info("Object requestnode " + requestnode);
			String jsonldResult = mapper.writeValueAsString(input);
			return new Data<>(jsonldResult.replace("<@type>", type));
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new TransformationException(ex.getMessage(), ex, ErrorCode.JSON_TO_JSONLD_TRANFORMATION_ERROR);
		}
	}

	private String getTypeFromNode(ObjectNode requestNode) throws JsonProcessingException {
		String rootValue = "";
		if (requestNode.isObject()) {
			logger.info("root node to set as type " + requestNode.fields().next().getKey());
			rootValue = requestNode.fields().next().getKey();
		}
		return rootValue;
	}

	private void appendSuffix(ObjectNode requestNode, String prefix) {

		requestNode.fields().forEachRemaining(entry -> {
			JsonNode entryValue = entry.getValue();
			if (entryValue.isValueNode() && nodeTypes.contains(entry.getKey())) {
				String defaultValue = prefix + entryValue.asText();
				requestNode.put(entry.getKey(), defaultValue);

			} else if (entryValue.isObject()) {
				appendSuffix((ObjectNode) entry.getValue(), prefix);
			} else if (entryValue.isArray()) {
				ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
				for (int i = 0; i < entryValue.size(); i++) {
					if (entryValue.get(i).isTextual())
						arrayNode.add(entryValue.get(i).asText().replaceFirst("", prefix));
					else
						appendSuffix((ObjectNode) entryValue.get(i), prefix);

				}
				replaceArrayNode(requestNode, entry.getKey(), arrayNode);

			}

		});
	}

	public static void replaceArrayNode(ObjectNode parent, String fieldName, ArrayNode newarrayNode) {
		if (newarrayNode.size() > 0)
			parent.set(fieldName, newarrayNode);
	}

	private void setNodeTypeToAppend(ObjectNode fieldObjects) {
		ObjectNode context = (ObjectNode) fieldObjects.path(JsonldConstants.CONTEXT);
		context.fields().forEachRemaining(entry -> {
			if (entry.getValue().has(JsonldConstants.TYPE)
					&& entry.getValue().get(JsonldConstants.TYPE).asText().equalsIgnoreCase(JsonldConstants.ID)) {
				nodeTypes.add(entry.getKey());
			}
		});
		logger.info("nodeType size " + nodeTypes.size());
	}

	private void setSufix(String type) {
		sufix = type.toLowerCase() + SEPERATOR;
	}

	@Override
	public void setPurgeData(List<String> keyToPruge) {

	}

}
