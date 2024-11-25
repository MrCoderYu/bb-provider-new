package io.bbex.bb.server.grpc;

import com.toobit.tbsc.spring.boot.annotation.GrpcService;
import io.bbex.base.account.CancelSymbolOrdersReply;
import io.bbex.base.account.TestReply;
import io.bbex.base.account.TestServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
@Slf4j
public class TestGrpcImpl extends TestServiceGrpc.TestServiceImplBase {

    @Autowired
    private GrpcTestService grpcTestService;

    @Override
    public void test(io.bbex.base.account.TestRequest request,
                     io.grpc.stub.StreamObserver<io.bbex.base.account.TestReply> responseObserver) {
        log.info("test method,accountId:{}", request.getAccountId());
//        TestReply testReply = grpcTestService.test(request);
        responseObserver.onNext(TestReply.newBuilder().build());
        responseObserver.onCompleted();
    }


}
