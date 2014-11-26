<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head></head>
<body>
	<c:forEach items="${avisos}" var="item">
		<div class="col-sm-6 col-md-3" style="padding-bottom: 15px">
			<div class="caption">
				<p>${item.titulo}</p>
				<p>${item.contenidoAviso}</p>
				<p>${item.tipoAviso}</p>
				<p>${item.etiqueta}</p>
				<p>
					<a class="btn btn-success"
						href="<c:url value="/avisos/gestor/editar?id=${item.postInternalId}"></c:url>">
						<span class="glyphicon glyphicon-edit"></span>
					</a>
				</p>
				<p><a
					href="<c:url value="/avisos/ver/individual?id=${item.postInternalId}"></c:url>"
					class="btn btn-primary"> <span
						class="glyphicon glyphicon-info-sign"></span>
				</a></p>
				<p><a
					href="<c:url value="/avisos/gestor/eliminar?id=${item.postInternalId}"></c:url>"
					class="btn btn-danger"><span
						class="glyphicon glyphicon-remove-sign"></span></a></p>
						<hr>
				</p>
			</div>
		</div>
	</c:forEach>
</body>
</html>
